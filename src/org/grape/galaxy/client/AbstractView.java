package org.grape.galaxy.client;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractView<MT, PVT extends AbstractView> {

	protected PVT parentView;
	protected List<AbstractView> childViews = new ArrayList<AbstractView>();
	protected MT model;
	
	protected boolean destroyed = false;

	public AbstractView(PVT parentView, MT model) {
		this.parentView = parentView;
		this.model = model;
		parentView.childViews.add(this);
	}

	public AbstractView(MT model) {
		this.model = model;
	}

	public PVT getParentView() {
		return parentView;
	}

	public MT getModel() {
		return model;
	}

	public boolean isDestroyed() {
		return destroyed;
	}
	
	protected void onResize() {
		for (AbstractView child : childViews) {
			child.onResize();
		}
	}

	protected void update(double dt, double time) {
		for (AbstractView child : childViews) {
			child.update(dt, time);
		}
	}

	public void destroy() {
		parentView.childViews.remove(this);
		for (AbstractView childView : new ArrayList<AbstractView>(childViews)) {
			childView.destroy();
		}
		this.destroyed = true;
	}
}
