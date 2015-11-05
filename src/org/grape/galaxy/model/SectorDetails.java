package org.grape.galaxy.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class SectorDetails implements Serializable {

	private static final long serialVersionUID = -1739445480693844034L;

	private boolean actual;

	private List<PlanetDetails> planetsDetails;
	private List<Transportation> transportations;

	private List<PlanetDetails> addititonalPlanetsDetails;

	public boolean isActual() {
		return actual;
	}

	public void setActual(boolean actual) {
		this.actual = actual;
	}

	public List<PlanetDetails> getPlanetsDetails() {
		return planetsDetails;
	}

	public void setPlanetsDetails(List<PlanetDetails> planetsDetails) {
		this.planetsDetails = planetsDetails;
	}

	public List<Transportation> getTransportations() {
		return transportations;
	}

	public void setTransportations(List<Transportation> transportations) {
		this.transportations = transportations;
	}

	public List<PlanetDetails> getAddititonalPlanetsDetails() {
		return addititonalPlanetsDetails;
	}

	public void setAddititonalPlanetsDetails(
			List<PlanetDetails> addititonalPlanetsDetails) {
		this.addititonalPlanetsDetails = addititonalPlanetsDetails;
	}
	
	public void merge(SectorDetails other) {
		actual = other.actual;
		planetsDetails = other.planetsDetails;
		mergeTransportations(other.getTransportations());
		
		addititonalPlanetsDetails = other.addititonalPlanetsDetails;
	}

	public void mergeTransportations(List<Transportation> others) {
		for (Iterator<Transportation> iter = transportations.iterator(); iter
				.hasNext();) {
			Transportation transportation = iter.next();
			int otherIndex = others.indexOf(transportation);
			if (otherIndex != -1) {
				transportation.merge(others.get(otherIndex));
			} else {
				iter.remove();
			}
		}
		for (Transportation other : others) {
			if (!transportations.contains(other)) {
				transportations.add(other);
			}
		}
	}
}
