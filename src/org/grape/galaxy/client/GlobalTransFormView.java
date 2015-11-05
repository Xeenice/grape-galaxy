package org.grape.galaxy.client;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Slider;
import com.smartgwt.client.widgets.events.ValueChangedEvent;
import com.smartgwt.client.widgets.events.ValueChangedHandler;

public class GlobalTransFormView extends AbstractView<Galaxy, GalaxyView> {

	static final String TRANS_TYPE_FLEET = "fleet";
	static final String TRANS_TYPE_RESOURCE = "resource";

	private HTMLFlow htmlFlow;

	private int amount = 50;
	private String transType = TRANS_TYPE_FLEET;
	private Planet sourcePlanet = null;
	private Planet targetPlanet = null;

	private Dialog transPercentDialog;

	private boolean sourcePlanetSelection = false;
	private boolean targetPlanetSelection = false;

	public GlobalTransFormView(final GalaxyView parentView, final Galaxy model) {
		super(parentView, model);

		htmlFlow = new HTMLFlow(
				"Перебросить "
						+ "<a id=\"globalTransPercent\" href=\"javascript: void(0);\" onclick=\"globalTransPercentHandler(event)\">50%</a> "
						+ "от доступного кол-ва "
						+ "<a id=\"globalTransType\" href=\"javascript: void(0);\" onclick=\"globalTransTypeHandler(event)\">кораблей</a> c "
						+ "<a id=\"globalTransSourceName\" href=\"javascript: void(0);\" onclick=\"globalTransSourcePlanetHandler(event)\">Планеты А</a> "
						+ "на <a id=\"globalTransTargetName\" href=\"javascript: void(0);\" onclick=\"globalTransTargetPlanetHandler(event)\">Планету Б</a>. "
						+ "<a id=\"globalTransAccept\" class=\"invisible\" href=\"javascript: void(0);\" onclick=\"globalTransAcceptHandler(event)\">Подтвердить переброску.</a>");
		htmlFlow.setWidth100();
		htmlFlow.setHeight100();

		transPercentDialog = new Dialog();
		transPercentDialog.setAutoCenter(true);
		transPercentDialog.setIsModal(false);
		transPercentDialog.setShowHeader(false);
		transPercentDialog.setShowEdges(false);
		transPercentDialog.setShowToolbar(false);
		transPercentDialog.setWidth(150);
		transPercentDialog.setHeight(20);
		transPercentDialog.setOverflow(Overflow.HIDDEN);
		transPercentDialog.setShowEdges(true);

		transPercentDialog.hide();

		Slider transPercentSlider = new Slider();
		transPercentSlider.setShowTitle(false);
		transPercentSlider.setShowRange(false);
		transPercentSlider.setShowValue(false);
		transPercentSlider.setVertical(false);
		transPercentSlider.setMinValue(10);
		transPercentSlider.setMaxValue(100);
		transPercentSlider.setNumValues(10);
		transPercentSlider.setValue(50);
		transPercentSlider.setLength(150);
		transPercentSlider.addValueChangedHandler(new ValueChangedHandler() {

			@Override
			public void onValueChanged(ValueChangedEvent event) {
				amount = event.getValue();
				Element globalTransPercentEl = Document.get().getElementById(
						"globalTransPercent");
				if (globalTransPercentEl != null) {
					globalTransPercentEl.setInnerHTML(amount + "%");
				}
			}
		});

		transPercentDialog.addItem(transPercentSlider);

		parentView.getGlobalTransSection().getItems()[0].addChild(htmlFlow);

		updateOkButton();

		createHandlers();
	}

	public HTMLFlow getHtmlFlow() {
		return htmlFlow;
	}
	
	public void reset() {
		resetSourcePlanet();
	}
	
	private native void createHandlers() /*-{
		var self = this;
		$wnd.globalTransPercentHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransPercent(II)(event.clientX, event.clientY);
		};
		$wnd.globalTransTypeHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransType()();
		};
		$wnd.globalTransSourcePlanetHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransSourcePlanet()();
		};
		$wnd.globalTransTargetPlanetHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransTargetPlanet()();
		};
		$wnd.globalTransCancelHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransCancel()();
		};
		$wnd.globalTransAcceptHandler = function(event) {
			self.@org.grape.galaxy.client.GlobalTransFormView::handleTransAccept()();
		};
	}-*/;

	private void handleTransPercent(int clientX, int clientY) {
		if (transPercentDialog.isVisible()) {
			transPercentDialog.hide();
		} else {
			transPercentDialog.show();
			transPercentDialog.moveTo(clientX - 75, clientY + 10);
		}

		updateOkButton();
	}

	private void handleTransType() {
		if (TRANS_TYPE_FLEET.equals(transType)) {
			selectTransType(TRANS_TYPE_RESOURCE);
		} else if (TRANS_TYPE_RESOURCE.equals(transType)) {
			selectTransType(TRANS_TYPE_FLEET);
		}

		updateOkButton();
		transPercentDialog.hide();
	}

	private void handleTransSourcePlanet() {
		if (sourcePlanetSelection) {
			parentView.clearStatusMessage();
			resetSourcePlanet();
		} else {
			startSourcePlanetSelection();
		}

		updateOkButton();
		transPercentDialog.hide();
	}

	private void handleTransTargetPlanet() {
		if (sourcePlanet == null) {
			SC.say("Пожалуйста, выберете сначала \"Планету А\"!");
			return;
		}

		if (targetPlanetSelection) {
			resetTargetPlanet();
		} else {
			startTargetPlanetSelection();
		}

		updateOkButton();
		transPercentDialog.hide();
	}

	private void handleTransAccept() {
		if (!isReady()) {
			return;
		}

		final Planet currentSourcePlanet = this.sourcePlanet;
		if (TRANS_TYPE_FLEET.equals(transType)) {
			int unitCount = (int) Math.round(sourcePlanet.getOrbitUnitCount()
					* (double) amount / 100d);
			if (unitCount > 0) {
				parentView.requestFleetTransportation(sourcePlanet,
						targetPlanet, unitCount, true, new Runnable() {

							@Override
							public void run() {
								parentView.changeSector(
										currentSourcePlanet.getSector(),
										currentSourcePlanet);
							}
						});
			} else {
				SC.warn("На орбите планеты нет кораблей для переброски!");
			}
		} else if (TRANS_TYPE_RESOURCE.equals(transType)) {
			double resourceCount = (sourcePlanet.getResourceCount()
					* (double) amount / 100d);
			if (resourceCount > Constants.EPS) {
				parentView.requestResourceTransportation(sourcePlanet,
						targetPlanet, resourceCount, true, new Runnable() {

							@Override
							public void run() {
								parentView.changeSector(
										currentSourcePlanet.getSector(),
										currentSourcePlanet);
							}
						});
			} else {
				SC.warn("На планете нет ресурсов для переброски!");
			}
		}

		resetSourcePlanet();
	}

	private void handleTransCancel() {
		selectTransType(TRANS_TYPE_FLEET);
	}

	void selectTransType(String transType) {
		this.transType = transType;
		if (TRANS_TYPE_FLEET.equals(transType)) {
			Document.get().getElementById("globalTransType")
					.setInnerHTML("кораблей");
		} else if (TRANS_TYPE_RESOURCE.equals(transType)) {
			Document.get().getElementById("globalTransType")
					.setInnerHTML("ресурсов");
		}

		resetSourcePlanet();
	}

	void selectSourcePlanet(Planet planet) {
		sourcePlanetSelection = false;
		sourcePlanet = planet;
		if (planet != null) {
			Document.get().getElementById("globalTransSourceName")
					.setInnerHTML(planet.getText());
		} else {
			Document.get().getElementById("globalTransSourceName")
					.setInnerHTML("Планеты А");
		}

		resetTargetPlanet();
	}

	private void startSourcePlanetSelection() {
		sourcePlanetSelection = true;
		Document.get().getElementById("globalTransSourceName")
				.setInnerHTML(".........");
		sourcePlanet = null;

		resetTargetPlanet();

		parentView
				.setStatusMessage("<span onclick=\"globalTransSourcePlanetHandler(event)\" class=\"planet-selection-status\">Выберите Планету А</span>");
		parentView.showStatusMessage();

		parentView.setPlanetSelectionFilter(new PlanetSelectionFilter() {

			@Override
			public boolean canSelect(Planet planet) {
				if (UserContainer.get().getUserId().equals(planet.getOwnerId())) {
					if (TRANS_TYPE_FLEET.equals(transType)) {
						if (planet.getOrbitUnitCount() > 0) {
							return true;
						}
					} else if (TRANS_TYPE_RESOURCE.equals(transType)) {
						if (planet.getResourceCount() > Constants.EPS) {
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public void onSelect(Planet planet) {
				sourcePlanetSelection = false;
				sourcePlanet = planet;
				if (planet != null) {
					Document.get().getElementById("globalTransSourceName")
							.setInnerHTML(planet.getText());
				} else {
					Document.get().getElementById("globalTransSourceName")
							.setInnerHTML("Планеты А");
				}

				parentView.clearStatusMessage();
				parentView.hideStatusMessage();

				updateOkButton();

				if (!isReady()) {
					new Timer() {

						@Override
						public void run() {
							startTargetPlanetSelection();
						}

					}.schedule(100);
				}
			}
		});

		ViewUtils.attractAttention(Document.get().getElementById("o3d"));
	}

	void startTargetPlanetSelection(String transType, Planet sourcePlanet) {
		selectTransType(transType);
		selectSourcePlanet(sourcePlanet);

		startTargetPlanetSelection();
	}

	private void startTargetPlanetSelection() {
		targetPlanetSelection = true;
		Document.get().getElementById("globalTransTargetName")
				.setInnerHTML(".........");
		targetPlanet = null;

		parentView
				.setStatusMessage("<span onclick=\"globalTransTargetPlanetHandler(event)\" class=\"planet-selection-status\">Выберите Планету Б</span>");
		parentView.showStatusMessage();

		parentView.setPlanetSelectionFilter(new PlanetSelectionFilter() {

			@Override
			public boolean canSelect(Planet planet) {
				return ((sourcePlanet.getIndex() != planet.getIndex()) && (TRANS_TYPE_FLEET
						.equals(transType) || (TRANS_TYPE_RESOURCE
						.equals(transType) && UserContainer.get().getUserId()
						.equals(planet.getOwnerId()))));
			}

			@Override
			public void onSelect(Planet planet) {
				targetPlanetSelection = false;
				targetPlanet = planet;
				if (planet != null) {
					Document.get().getElementById("globalTransTargetName")
							.setInnerHTML(planet.getText());
				} else {
					Document.get().getElementById("globalTransTargetName")
							.setInnerHTML("Планеты А");
				}

				parentView.clearStatusMessage();
				parentView.hideStatusMessage();

				updateOkButton();
			}
		});

		ViewUtils.attractAttention(Document.get().getElementById("o3d"));
	}

	private void updateOkButton() {
		Element transAcceptEl = Document.get().getElementById(
				"globalTransAccept");
		if (transAcceptEl == null) {
			return;
		}

		if (isReady()) {
			transAcceptEl.removeClassName("invisible");
			parentView
					.setStatusMessage("<span onclick=\"globalTransAcceptHandler(event)\" class=\"planet-selection-status\">Подтвердите переброску</span>");
			parentView.showStatusMessage();

			ViewUtils.attractAttention(Document.get().getElementById("o3d"));
		} else {
			transAcceptEl.addClassName("invisible");
		}
	}

	private boolean isReady() {
		return ((amount > 0) && (transType != null) && (sourcePlanet != null)
				&& (targetPlanet != null) && (sourcePlanet != targetPlanet) && UserContainer
				.get().getUserId().equals(sourcePlanet.getOwnerId()));
	}

	private void resetSourcePlanet() {
		sourcePlanetSelection = false;
		parentView.setPlanetSelectionFilter(null);
		Document.get().getElementById("globalTransSourceName")
				.setInnerHTML("Планеты А");
		sourcePlanet = null;

		parentView.clearStatusMessage();
		parentView.hideStatusMessage();

		resetTargetPlanet();

		updateOkButton();
	}

	private void resetTargetPlanet() {
		targetPlanetSelection = false;
		parentView.setPlanetSelectionFilter(null);
		Document.get().getElementById("globalTransTargetName")
				.setInnerHTML("Планету Б");
		targetPlanet = null;

		parentView.clearStatusMessage();
		parentView.hideStatusMessage();

		updateOkButton();
	}
}
