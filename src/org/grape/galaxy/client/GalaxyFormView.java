package org.grape.galaxy.client;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class GalaxyFormView extends AbstractView<Galaxy, GalaxyView> {

	private TabSet mainTabSet;
	private Tab sectorTab;
	private Tab ratingTab;

	private Button goHomeButton;

	private Canvas sectorLeftPanel;
	private Canvas sectorCenterPanel;
	private Canvas sectorRightPanel;

	private SectionStackSection planetsGridSection;
	private SectionStackSection transportationsGridSection;
	private SectionStackSection chatSection;

	private ChatView chatView;

	private FeedbackView feedbackView;

	private SectionStackSection planetSection;
	private SectionStackSection globalTransSection;

	private Button galaxyMapButton;

	private GlobalTransFormView globalTransFormView;

	private boolean sceneStatusEnabled = false;
	private boolean actualizationEnabled = false;
	private Timer statusBlinkTimer;

	public GalaxyFormView(GalaxyView parentView, Galaxy model) {
		super(parentView, model);

		createGlobalFuncs();
		createBodyPanel();
	}

	Tab getSectorTab() {
		return sectorTab;
	}

	Tab getRatingTab() {
		return ratingTab;
	}

	Button getGoHomeButton() {
		return goHomeButton;
	}

	Canvas getSectorLeftPanel() {
		return sectorLeftPanel;
	}

	Canvas getSectorCenterPanel() {
		return sectorCenterPanel;
	}

	Canvas getSectorRightPanel() {
		return sectorRightPanel;
	}

	SectionStackSection getPlanetsGridSection() {
		return planetsGridSection;
	}

	SectionStackSection getTransportationsGridSection() {
		return transportationsGridSection;
	}

	SectionStackSection getChatSection() {
		return chatSection;
	}

	ChatView getChatView() {
		return chatView;
	}

	SectionStackSection getPlanetSection() {
		return planetSection;
	}

	SectionStackSection getGlobalTransSection() {
		return globalTransSection;
	}

	GlobalTransFormView getGlobalTransFormView() {
		return globalTransFormView;
	}

	Button getGalaxyMapButton() {
		return galaxyMapButton;
	}

	private GalaxyView getGalaxyView() {
		return getParentView();
	}

	private void createBodyPanel() {
		Canvas bodyPanel = new Canvas();
		bodyPanel.setWidth100();
		bodyPanel.setHeight100();
		bodyPanel.setStyleName("body");

		Canvas mainPanel = new Canvas();
		mainPanel.setWidth100();
		mainPanel.setHeight100();
		mainPanel.setStyleName("topimage");

		VLayout mainLayout = new VLayout();
		mainLayout.setWidth100();
		mainLayout.setHeight100();

		createHeaderPanel(mainLayout);
		createMainTabSet(mainLayout);

		HLayout sectorLayout = new HLayout();
		sectorLayout.setWidth100();
		sectorLayout.setHeight100();

		createSectorLeftPanel(sectorLayout);
		createSectorCenterPanel(sectorLayout);
		createSectorRightPanel(sectorLayout);

		sectorTab.setPane(sectorLayout);

		final RatingView ratingView = new RatingView();
		ratingTab.setPane(ratingView.getPane());
		mainTabSet.addTabSelectedHandler(new TabSelectedHandler() {

			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (event.getTab() == ratingTab) {
					ratingView.notifyShowed();
				}
			}
		});

		createFooterPanel(mainLayout);

		mainPanel.addChild(mainLayout);
		bodyPanel.addChild(mainPanel);

		bodyPanel.draw();

		sectorCenterPanel.addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				getGalaxyView().onResize();
			}
		});
		getGalaxyView().onResize();
	}

	private void createHeaderPanel(VLayout mainLayout) {
		HTMLFlow headerHTML = new HTMLFlow("<h1>Галактика</h1>");
		headerHTML.setWidth100();
		headerHTML.setHeight(50);
		headerHTML.setStyleName("header");

		mainLayout.addMember(headerHTML);
	}

	private void createMainTabSet(VLayout mainLayout) {
		mainTabSet = new TabSet();
		mainTabSet.setTabBarPosition(Side.TOP);
		mainTabSet.setTabBarThickness(25);
		mainTabSet.setTabBarAlign(Side.LEFT);
		mainTabSet.setWidth100();
		mainTabSet.setHeight("*");

		sectorTab = new Tab("Сектор", "[SKIN]/headerIcons/settings.png");
		sectorTab.setIconWidth(15);
		sectorTab.setIconHeight(15);
		mainTabSet.addTab(sectorTab);

		ratingTab = new Tab("Рейтинг", "[SKIN]/headerIcons/calculator.png");
		ratingTab.setIconWidth(15);
		ratingTab.setIconHeight(15);
		mainTabSet.addTab(ratingTab);

		createFeedbackView();

		Button feedbackButton = new Button("Контакт");
		feedbackButton.setIcon("[SKIN]/headerIcons/mail.png");
		feedbackButton.setIconWidth(15);
		feedbackButton.setIconHeight(15);
		feedbackButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				feedbackView.show();
			}
		});
		goHomeButton = new Button("На базу");
		goHomeButton.setIcon("[SKIN]/headerIcons/home.png");
		goHomeButton.setIconWidth(15);
		goHomeButton.setIconHeight(15);
		goHomeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PlanetDetails homePlanetDetails = UserContainer.get()
						.getHomePlanetDetails();
				if (homePlanetDetails != null) {
					Planet homePlanet = Galaxy.get().getPlanet(
							homePlanetDetails.getIndex());
					getGalaxyView().changeSector(homePlanet.getSector(),
							homePlanet);
					mainTabSet.selectTab(0);
				}
			}
		});
		Button refreshButton = new Button("Обновить");
		refreshButton.setIcon("[SKIN]/headerIcons/refresh.png");
		refreshButton.setIconWidth(15);
		refreshButton.setIconHeight(15);
		refreshButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (getGalaxyView().refreshActiveSectorDetails()) {
					mainTabSet.selectTab(0);
				}
			}
		});
		galaxyMapButton = new Button("Карта");
		galaxyMapButton.setIcon("[SKIN]/headerIcons/find.png");
		galaxyMapButton.setIconWidth(15);
		galaxyMapButton.setIconHeight(15);
		galaxyMapButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (getGalaxyView().isGalaxyMapVisible()) {
					getGalaxyView().hideGalaxyMap();
				} else {
					getGalaxyView().showGalaxyMap();
				}
			}
		});
		Button helpButton = new Button("Как играть?");
		helpButton.setIcon("[SKIN]/headerIcons/help.png");
		helpButton.setIconWidth(15);
		helpButton.setIconHeight(15);
		helpButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Location.assign("/help/help.html");
			}
		});

		mainTabSet.setTabBarControls(TabBarControls.TAB_SCROLLER,
				TabBarControls.TAB_PICKER, refreshButton, galaxyMapButton,
				goHomeButton, feedbackButton, helpButton);

		mainLayout.addMember(mainTabSet);
	}

	private void createSectorLeftPanel(HLayout sectorLayout) {
		sectorLeftPanel = new Canvas();
		sectorLeftPanel.setWidth(300);
		sectorLeftPanel.setMinWidth(100);
		sectorLeftPanel.setShowResizeBar(true);

		SectionStack sectorLeftSections = new SectionStack();
		sectorLeftSections.setVisibilityMode(VisibilityMode.MULTIPLE);
		sectorLeftSections.setAnimateSections(true);
		sectorLeftSections.setWidth100();
		sectorLeftSections.setHeight100();

		planetsGridSection = new SectionStackSection("Планеты");
		planetsGridSection.setCanCollapse(true);
		planetsGridSection.setExpanded(true);
		Canvas planetsGridSectionItem = new Canvas();
		planetsGridSectionItem.setPadding(0);
		planetsGridSectionItem.setMargin(0);
		planetsGridSectionItem.setWidth100();
		planetsGridSectionItem.setHeight100();
		planetsGridSection.addItem(planetsGridSectionItem);
		sectorLeftSections.addSection(planetsGridSection);

		transportationsGridSection = new SectionStackSection("Переброски");
		transportationsGridSection.setCanCollapse(true);
		transportationsGridSection.setExpanded(true);
		transportationsGridSection.setResizeable(true);
		Canvas transportationsGridSectionItem = new Canvas();
		transportationsGridSectionItem.setPadding(0);
		transportationsGridSectionItem.setMargin(0);
		transportationsGridSectionItem.setWidth100();
		transportationsGridSectionItem.setHeight100();
		transportationsGridSection.addItem(transportationsGridSectionItem);
		sectorLeftSections.addSection(transportationsGridSection);

		createChatView(sectorLeftSections);

		sectorLeftPanel.addChild(sectorLeftSections);

		sectorLayout.addMember(sectorLeftPanel);
	}

	private void createChatView(SectionStack sectorLeftSections) {
		chatSection = new SectionStackSection();
		chatSection.setCanCollapse(true);
		chatSection.setExpanded(true);
		chatSection.setResizeable(true);

		chatView = new ChatView(chatSection);

		sectorLeftSections.addSection(chatSection);
	}

	private void createFeedbackView() {
		feedbackView = new FeedbackView();
	}

	private void createSectorCenterPanel(HLayout sectorLayout) {
		sectorCenterPanel = new HTMLFlow(
				"<div id=\"scene\" style=\"position: relative; left: 0px; top: 0px; width: 100%; height: 100%; padding: 0; margin: 0;\">"
						+ "<div id=\"o3d\" style=\"width: 100px; height: 100px; padding: 0; margin: 0;\"></div>"
						+ "<div id=\"sceneStatus\" class=\"invisible\" style=\"position: absolute; right: 2px; top: 4px; width: auto; margin 0; padding: 2px;\"></div></div>");
		sectorCenterPanel.setWidth("*");
		sectorCenterPanel.setMinWidth(100);
		sectorCenterPanel.setOverflow(Overflow.HIDDEN);
		sectorCenterPanel.setStyleName("center");
		sectorCenterPanel.setShowResizeBar(true);
		sectorCenterPanel.setResizeBarTarget("next");

		sectorLayout.addMember(sectorCenterPanel);
	}

	private void createSectorRightPanel(HLayout sectorLayout) {
		sectorRightPanel = new Canvas();
		sectorRightPanel.setWidth(300);

		SectionStack sectorRightSections = new SectionStack();
		sectorRightSections.setVisibilityMode(VisibilityMode.MULTIPLE);
		sectorRightSections.setAnimateSections(true);
		sectorRightSections.setWidth100();
		sectorRightSections.setHeight100();
		planetSection = new SectionStackSection("");
		planetSection.setCanCollapse(false);
		planetSection.setExpanded(true);
		Canvas planetSectionItem = new Canvas();
		planetSectionItem.setPadding(0);
		planetSectionItem.setMargin(0);
		planetSectionItem.setWidth100();
		planetSectionItem.setHeight100();
		planetSectionItem.setOverflow(Overflow.AUTO);
		planetSection.addItem(planetSectionItem);
		sectorRightSections.addSection(planetSection);
		globalTransSection = new SectionStackSection(
				"Форма переброски флота/ресурсов");
		globalTransSection.setCanCollapse(false);
		globalTransSection.setResizeable(true);
		globalTransSection.setExpanded(true);
		Canvas globalTransSectionItem = new Canvas();
		globalTransSectionItem.setPadding(0);
		globalTransSectionItem.setMargin(0);
		globalTransSectionItem.setWidth100();
		globalTransSectionItem.setHeight100();
		globalTransSectionItem.setOverflow(Overflow.AUTO);
		globalTransSection.addItem(globalTransSectionItem);
		sectorRightSections.addSection(globalTransSection);

		sectorRightPanel.addChild(sectorRightSections);

		sectorLayout.addMember(sectorRightPanel);
	}

	private void createFooterPanel(VLayout mainLayout) {
		HTMLFlow footerHTML = new HTMLFlow(
				"<span>&copy; 2011 Grape</span>"
						+ "<a class=\"rss\" href=\"http://feeds.feedburner.com/grape-galaxy\" rel=\"alternate\" type=\"application/rss+xml\"><img src=\"/images/feed-14x14.png\" alt=\"Подписаться\" title=\"Подписаться\"/></a>"
						+ "<a class=\"gg\" href=\"/res/galaxy/galaxy-gadget.gg\"><img src=\"/images/gg-14x14.png\" alt=\"Google Desktop Gadget\" title=\"Google Desktop Gadget\"/></a>"
						+ "<a class=\"help\" href=\"/help/help.html\"><img src=\"/images/help.png\" alt=\"Как играть?\" title=\"Как играть?\" width=\"14\" height=\"14\" /></a>");
		footerHTML.setWidth100();
		footerHTML.setHeight(20);
		footerHTML.setStyleName("footer");

		mainLayout.addMember(footerHTML);
	}

	private native void createGlobalFuncs() /*-{
		var galaxyView = this
				.@org.grape.galaxy.client.GalaxyFormView::getGalaxyView()();
		$wnd.selectPlanet = function(planetIndex) {
			galaxyView.@org.grape.galaxy.client.GalaxyView::changePlanet(Ljava/lang/String;)("" + planetIndex);
		};
	}-*/;

	@Override
	protected void onResize() {
		adjustCanvasSize();
		super.onResize();
	}

	private void adjustCanvasSize() {
		int w = sectorCenterPanel.getWidth();
		int h = sectorCenterPanel.getHeight();
		Element o3dDiv = Document.get().getElementById("o3d");
		o3dDiv.setAttribute("style", "width" + w + "px; height: " + h + "px");
		if (o3dDiv.getElementsByTagName("canvas").getLength() > 0) {
			Element o3dCanvas = o3dDiv.getElementsByTagName("canvas")
					.getItem(0);
			o3dCanvas.setAttribute("width", "" + w);
			o3dCanvas.setAttribute("height", "" + h);
			adjustCanvasSizeInternals(w, h);
		}
	}

	private native void adjustCanvasSizeInternals(int w, int h) /*-{
		if ($wnd.g_client.gl) {
			$wnd.g_client.gl.displayInfo = {
				width : "" + w,
				height : "" + h
			};
		}
	}-*/;

	public void showStatusMessage() {
		if (!sceneStatusEnabled) {
			final Element statusEl = Document.get().getElementById(
					"sceneStatus");
			if (statusEl != null) {
				if (statusBlinkTimer == null) {
					statusBlinkTimer = new Timer() {

						private boolean show;

						@Override
						public void run() {
							if (show) {
								statusEl.addClassName("transparent");
							} else {
								statusEl.removeClassName("transparent");
							}
							show = !show;
						}
					};
				} else {
					try {
						statusBlinkTimer.cancel();
					} catch (Exception nothing) {
					}
				}
				statusBlinkTimer
						.scheduleRepeating(ViewConstants.STATUS_MESSAGE_BLINK_TIME_MILLIS);
				statusEl.removeClassName("invisible");
			}
			sceneStatusEnabled = true;
		}
	}

	public void hideStatusMessage() {
		if (sceneStatusEnabled) {
			Element statusEl = Document.get().getElementById("sceneStatus");
			if (statusEl != null) {
				if (statusBlinkTimer != null) {
					statusBlinkTimer.cancel();
					statusBlinkTimer = null;
				}
				statusEl.addClassName("invisible");
			}
			sceneStatusEnabled = false;
		}
	}

	public void setStatusMessage(String message) {
		if (message == null) {
			message = "";
		}
		Element statusEl = Document.get().getElementById("sceneStatus");
		if (statusEl != null) {
			statusEl.setInnerHTML(message);
		}
	}

	public void clearStatusMessage() {
		setStatusMessage(null);
	}

	public void updateActualizationState(boolean actual) {
		if (!actual) {
			globalTransSection.setHidden(true);
			setStatusMessage("<span class=\"status\">Актуализация...</span>");
			showStatusMessage();
			actualizationEnabled = true;
		} else if (actualizationEnabled) {
			globalTransSection.setHidden(false);
			clearStatusMessage();
			hideStatusMessage();
			actualizationEnabled = false;
		}
	}

	public void checkEula(final EulaListener listener) {
		GalaxyServiceProvider.get().isEulaAccepted(
				new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							listener.accepted();
						} else {
							showEula(listener);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Ошибка: " + caught.getLocalizedMessage());
					}
				});
	}

	private void showEula(final EulaListener listener) {
		final Window window = new Window();
		window.setWidth(700);
		window.setHeight(500);
		window.setTitle("Лицензионное соглашение с конечным пользователем");
		window.centerInPage();
		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setShowMinimizeButton(false);
		window.setShowCloseButton(false);

		HTMLPane eulaText = new HTMLPane();
		eulaText.setWidth100();
		eulaText.setHeight100();
		eulaText.setPadding(10);
		eulaText.setContents(Document.get().getElementById("eula")
				.getInnerHTML());

		Button enterButton = new Button();
		enterButton.setTitle("Я принимаю условия лицензионного соглашения");
		enterButton.setMargin(10);
		enterButton.setWidth(300);
		enterButton.setHeight(40);
		enterButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				window.destroy();
				GalaxyServiceProvider.get().acceptEula(
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								listener.accepted();
							}

							@Override
							public void onFailure(Throwable caught) {
								SC.warn("Ошибка: "
										+ caught.getLocalizedMessage());
							}
						});
			}
		});

		window.addItem(eulaText);
		window.addItem(enterButton);

		window.show();
	}

}
