package ch.zhaw.pm4.compass.backend;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum UserRole {
	SOCIAL_WORKER("Social worker"), PARTICIPANT("Participant"), ADMIN("Admin"), NO_ROLE("Keine Rolle");

	public final String label;

	private UserRole(String label) {
		this.label = label;
	}
}
