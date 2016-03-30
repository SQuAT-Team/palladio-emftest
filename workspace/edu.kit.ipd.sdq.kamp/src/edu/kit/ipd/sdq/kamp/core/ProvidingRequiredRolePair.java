package edu.kit.ipd.sdq.kamp.core;

import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;

public class ProvidingRequiredRolePair {
	private ProvidedRole providedRole;
	private RequiredRole requiredRole;
	
	public ProvidingRequiredRolePair(ProvidedRole providedRole,
			RequiredRole requiredRole) {
		super();
		this.providedRole = providedRole;
		this.requiredRole = requiredRole;
	}
	
	public ProvidedRole getProvidedRole() {
		return providedRole;
	}
	public void setProvidedRole(ProvidedRole providedRole) {
		this.providedRole = providedRole;
	}
	public RequiredRole getRequiredRole() {
		return requiredRole;
	}
	public void setRequiredRole(RequiredRole requiredRole) {
		this.requiredRole = requiredRole;
	}
	
	
	
}
