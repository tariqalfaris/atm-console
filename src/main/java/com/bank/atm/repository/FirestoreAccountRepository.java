package com.bank.atm.repository;

import com.bank.atm.model.Account;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FirestoreAccountRepository implements AccountRepository {
    private static final String COLLECTION = "accounts";

    private final Firestore db;

    public FirestoreAccountRepository(String serviceAccountPath) throws IOException {
        try (FileInputStream in = new FileInputStream(serviceAccountPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
        this.db = FirestoreClient.getFirestore();
    }

    @Override
    public Account findByUsername(String username) {
        // Usernames are only letters, numbers and underscores, so anything else cannot exist
        if (username == null || !username.matches("[A-Za-z0-9_]+")) {
            return null;
        }
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(username).get().get();
            if (!doc.exists()) {
                return null;
            }
            Long balance = doc.getLong("balance");
            Long failed = doc.getLong("failedAttempts");
            return new Account(
                    username,
                    doc.getString("passwordHash"),
                    balance == null ? 0 : balance.intValue(),
                    Boolean.TRUE.equals(doc.getBoolean("locked")),
                    failed == null ? 0 : failed.intValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Account account) {
        Map<String, Object> data = new HashMap<>();
        data.put("passwordHash", account.getPasswordHash());
        data.put("balance", account.getBalance());
        data.put("locked", account.isLocked());
        data.put("failedAttempts", account.getFailedAttempts());
        try {
            db.collection(COLLECTION).document(account.getUsername()).set(data).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
