package ch.zhaw.pm4.compass.backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SystemService {
    @Autowired
    UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    public boolean isBackendReachable() {
        return true;
    }

    public boolean isDatabaseReachable() {
        try {
            Query query = entityManager.createNativeQuery("SELECT 1");
            query.setHint("javax.persistence.query.timeout", 3000);
            query.getSingleResult();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAuth0Reachable() {
        try {
            userService.getToken();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
