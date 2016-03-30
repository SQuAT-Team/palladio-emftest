package edu.kit.ipd.sdq.kamp.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class Activity {
	private ActivityType type;
	private ActivityElementType elementType;
	private String elementName;
	private EObject element;
	private BasicActivity basicActivity;
	private String description;
	private String affectingArchitectureElement;
	
	private Activity parentActivity;
	private List<Activity> subactivities;
	private List<Activity> followupActivities;

	public Activity(ActivityType type, ActivityElementType elementType,
			String elementName, BasicActivity basicActivity, String description, String affectingArchitectureElement) {
		this(type, elementType, elementName, basicActivity, description);
		this.affectingArchitectureElement = affectingArchitectureElement;
	}

	public Activity(ActivityType type, ActivityElementType elementType,
			String elementName, BasicActivity basicActivity, String description, EObject element) {
		this(type, elementType, elementName, basicActivity, description);
		this.element = element;
	}
	
	public Activity(ActivityType type, ActivityElementType elementType,
			String elementName, BasicActivity basicActivity, String description) {
		super();
		this.type = type;
		this.elementType = elementType;
		this.elementName = elementName;
		this.basicActivity = basicActivity;
		this.description = description;
		this.subactivities = new ArrayList<Activity>();
		this.followupActivities = new ArrayList<Activity>();
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public ActivityElementType getElementType() {
		return elementType;
	}

	public void setElementType(ActivityElementType elementType) {
		this.elementType = elementType;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public BasicActivity getBasicActivity() {
		return basicActivity;
	}

	public void setBasicActivity(BasicActivity basicActivity) {
		this.basicActivity = basicActivity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addSubactivity(Activity subactivity) {
		this.subactivities.add(subactivity);
		subactivity.setParentActivity(this);
	}

	public void removeSubactivity(Activity subactivity) {
		if (this.subactivities.contains(subactivity)) {
			this.subactivities.remove(subactivity);
			subactivity.setParentActivity(null);
		}
	}

	public void removeSubactivityAtIndex(int index) {
		if (index >= 0 && this.subactivities.size()>index) {
			Activity subactivity = this.subactivities.get(index);
			subactivity.setParentActivity(null);
			this.subactivities.remove(index);
		}
	}

	public void addFollowupactivity(Activity followupactivity) {
		this.followupActivities.add(followupactivity);
		followupactivity.setParentActivity(this);
	}
	
	public void addFollowupactivities(List<Activity> followupactivityList) {
		for (Activity activity : followupactivityList) {
			this.addFollowupactivity(activity);
		}
	}

	public void addSubactivities(List<Activity> subactivityList) {
		for (Activity activity : subactivityList) {
			this.addSubactivity(activity);
		}
	}

	public void removeFollowupactivity(Activity followupactivity) {
		if (this.followupActivities.contains(followupactivity)) {
			this.followupActivities.remove(followupactivity);
			followupactivity.setParentActivity(null);
		}
	}

	public void removeFollowupactivityAtIndex(int index) {
		if (index >= 0 && this.followupActivities.size()>index) {
			Activity followupactivity = this.followupActivities.get(index);
			followupactivity.setParentActivity(null);
			this.followupActivities.remove(index);
		}
	}

	public EObject getElement() {
		return element;
	}

	public List<Activity> getSubactivities() {
		return subactivities;
	}

	public List<Activity> getFollowupActivities() {
		return followupActivities;
	}

	public Activity getParentActivity() {
		return parentActivity;
	}

	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}

	public String getAffectingArchitectureElement() {
		return affectingArchitectureElement;
	}
	
	
}
