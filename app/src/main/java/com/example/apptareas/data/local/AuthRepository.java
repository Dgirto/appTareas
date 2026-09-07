package com.example.apptareas.data.local;

import android.content.Context;
import com.example.apptareas.model.UserEntity;
import com.example.apptareas.util.SecurityUtils;

public class AuthRepository {

    private Context context;
    private UserDao userDao;
    private UserEntity currentUser;

    public AuthRepository(Context context) {
        this.context = context;
        AppDatabase db = AppDatabase.getInstance(context);
        this.userDao = db.userDao();
        this.currentUser = null;
    }

    // === REGISTRO LOCAL ===
    public boolean registerLocal(String username, String email, String password) {
        try {
            // Verificar si el usuario ya existe
            UserEntity existingUser = userDao.getUserByEmail(email);
            if (existingUser != null) {
                return false; // Usuario ya existe
            }

            // Hashear contraseña
            String[] hashResult = SecurityUtils.hashPassword(password);
            String hash = hashResult[0];
            String salt = hashResult[1];

            UserEntity user = new UserEntity(
                    username,
                    email,
                    hash,
                    salt
            );

            userDao.insertUser(user);
            currentUser = user;
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === LOGIN LOCAL ===
    public boolean loginLocal(String email, String password) {
        try {
            UserEntity user = userDao.getUserByEmail(email);
            if (user == null) {
                return false; // Usuario no encontrado
            }

            // Verificar contraseña
            String storedHash = user.getPasswordHash();
            String salt = user.getSalt();

            if (!SecurityUtils.verifyPassword(password, storedHash, salt)) {
                return false; // Contraseña incorrecta
            }

            currentUser = user;
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === CERRAR SESIÓN ===
    public void logout() {
        currentUser = null;
    }

    // === VERIFICAR SESIÓN ===
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    // === OBTENER USUARIO ACTUAL ===
    public UserEntity getCurrentUser() {
        return currentUser;
    }
}