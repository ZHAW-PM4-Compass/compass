package ch.zhaw.pm4.compass.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Setter;

@Service
@Setter
public class SystemService {
	@Autowired
	private UserService userService;
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
