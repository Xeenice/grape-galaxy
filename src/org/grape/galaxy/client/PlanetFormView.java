package org.grape.galaxy.client;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.utils.StringUtils;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BlurbItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class PlanetFormView extends AbstractView<Planet, PlanetView> {

	private ListGridRecord listGridRecord;
	private DynamicForm form;

	private Element planetDetailsDiv;

	public PlanetFormView(PlanetView parentView, Planet model) {
		super(parentView, model);

		listGridRecord = new ListGridRecord();
		createForm();
		createPlanetDetailsDiv();

		updatePlanetDetailsRelatedElements();
	}

	public ListGridRecord getListGridRecord() {
		return listGridRecord;
	}

	public DynamicForm getForm() {
		return form;
	}

	private PlanetView getPlanetView() {
		return getParentView();
	}

	private SectorView getSectorView() {
		return getPlanetView().getParentView();
	}

	private GalaxyView getGalaxyView() {
		return getSectorView().getParentView();
	}

	private Sector getSector() {
		return getSectorView().getModel();
	}

	private void createForm() {
		form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setIsGroup(false);
		form.setNumCols(4);

		form.setItemHoverWidth(190);
		form.setItemHoverDelay(1000);
		form.setItemHoverOpacity(80);
		form.setItemHoverStyle("prompt");

		Validator nameValidator = new RegExpValidator(
				"^[A-ZА-Я][A-ZА-Яa-zа-я0-9 _\\-]+$");
		nameValidator
				.setErrorMessage("Имя планеты должно начинаться с заглавной буквы");
		Validator nameUniquenessValidator = new CustomValidator() {

			@Override
			protected boolean condition(Object value) {
				boolean ok = true;
				for (Planet planet : getSector().getPlanets()) {
					if (!planet.equals(getModel()) && isNamesSimilar(
							planet.getPlanetName(), (String) value)) {
						ok = false;
						break;
					}
				}
				return ok;
			}
			
			private boolean isNamesSimilar(String nameA, String nameB) {
				if (nameA == nameB) {
					return true;
				}
				if ((nameA == null) || (nameB == null)) {
					return false;
				}
				if (nameA.equalsIgnoreCase(nameB)) {
					return true;
				}
				nameA = nameA.trim().toLowerCase();
				nameB = nameB.trim().toLowerCase();
				if (nameA.contains(nameB) || nameB.contains(nameA)) {
					return true;
				}
				double l = Math.max(nameA.length(), nameB.length());
				double dist = StringUtils.getLevenshteinDistance(nameA, nameB);
				return ((dist / (l + 1)) < 0.3);
			}
		};
		nameUniquenessValidator
				.setErrorMessage("Планета с похожим именем уже есть в этом секторе");
		Validator canControlValidator = new CustomValidator() {

			@Override
			protected boolean condition(Object value) {
				return getSector().isDetailsActual();
			}
		};
		canControlValidator
				.setErrorMessage("Подождите, идет актуализация данных");

		TextItem nameField = new TextItem("planetName", "Имя");
		nameField.setColSpan("*");
		nameField.setLength(40);
		nameField.setValidateOnChange(true);
		nameField.setValidators(nameValidator, nameUniquenessValidator, canControlValidator);
		nameField.setKeyPressFilter("[A-ZА-Яa-zа-я0-9 _\\-]");
		FormItemIcon nameFieldIcon = new FormItemIcon();
		nameFieldIcon.setSrc("[SKIN]/headerIcons/double_arrow_right.png");
		nameFieldIcon.setShowOver(true);
		nameFieldIcon.setWidth(15);
		nameFieldIcon.setHeight(15);
		nameField.setIcons(nameFieldIcon);
		nameField.setPrompt("<p>Имя планеты</p>");
		nameField.addIconClickHandler(new IconClickHandler() {

			@Override
			public void onIconClick(IconClickEvent event) {
				if (event.getItem().validate()) {
					requestPlanetRename((String) event.getItem().getValue());
				}
			}
		});
		nameField.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ("Enter".equals(event.getKeyName())
						&& event.getItem().validate()) {
					requestPlanetRename((String) event.getItem().getValue());
				}
			}
		});
		LinkItem ownerField = new LinkItem("ownerName");
		ownerField.setTitle("Владелец");
		ownerField.setShowTitle(true);
		ownerField.setColSpan("*");
		ownerField.setPrompt("<p>Имя владельца планеты</p>");
		ownerField.setTarget("javascript");
		ownerField.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String ownerName = model.getOwnerName();
				if ((ownerName != null) && (ownerName.length() > 0)) {
					getGalaxyView().getChatView().sendPrivateMessage(ownerName);
				}
			}
		});

		HeaderItem paramsSection = new HeaderItem("paramsSection");
		paramsSection.setDefaultValue("Параметры");
		StaticTextItem resourceCountField = new StaticTextItem("resourceCount",
				"Ресурс");
		resourceCountField
				.setPrompt("<p>Кол-во ресурсов на планете</p><p class=\"description\">Ресурс постепенно вопсполняется. Скорость восполнения - "
						+ Constants.PLANET_RESOURCE_COUNT_GROW_VELOCITY
						+ " ед. ресурса за цикл. Используются для производства кораблей и защиты планеты</p>");
		StaticTextItem resourceCountLimitField = new StaticTextItem(
				"resourceCountLimit", "Макс.");
		resourceCountLimitField
				.setPrompt("<p>Максимально возможное количество ресурсов на планете</p>");
		StaticTextItem orbitUnitCountField = new StaticTextItem(
				"orbitUnitCount", "Корабли");
		orbitUnitCountField
				.setPrompt("<p>Кол-во кораблей на орбите планеты</p><p class=\"description\">Нужны для защиты границ и завоевания других планет</p>");
		StaticTextItem orbitUnitCountLimitField = new StaticTextItem(
				"orbitUnitCountLimit", "Макс.");
		orbitUnitCountLimitField
				.setPrompt("<p>Максимально возможное количество кораблей на орбите планеты</p>");
		StaticTextItem currentDefenceKField = new StaticTextItem(
				"currentDefenceK", "Защита");
		currentDefenceKField
				.setPrompt("<p>Показывает текущую степень защищённости планеты</p><p class=\"description\">&gt;&gt;1 хорошая защита</p>");
		StaticTextItem defenceKField = new StaticTextItem("defenceK", "Макс.");
		defenceKField
				.setPrompt("<p>Показывает максимальную степень защищённости планеты (в режиме защиты)</p><p class=\"description\">&gt;&gt;1 хорошая защита</p>");

		HeaderItem actionsSection = new HeaderItem("actionsSection");
		actionsSection.setDefaultValue("Приказы");
		LinkItem homeButton = new LinkItem("homeButton");
		homeButton.setShowTitle(false);
		homeButton.setLinkTitle("Развернуть базу");
		homeButton.setColSpan("*");
		homeButton.setValidators(canControlValidator);
		homeButton
				.setPrompt("<p>Может быть только одна базовая планета</p><p class=\"description\">Базовую планету невозможно завоевать, но можно опустошать</p>");
		homeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!event.getItem().validate() || destroyed)
					return;

				SC.ask("Вы уверены, что хотите развернуть базу на планете "
						+ model.getText()
						+ "? Вы можете иметь только одну базовую планету и не сможете"
						+ " изменять ее местоположение в дальнейшем. Базовую планету"
						+ " невозможно завоевать, но можно опустошать.",
						new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (!value) {
									return;
								}

								ViewUtils.blockUI();
								GalaxyServiceProvider.get().registerHomePlanet(
										model.getIndex(),
										new AsyncCallback<PlanetDetails>() {

											@Override
											public void onSuccess(
													PlanetDetails result) {
												if (destroyed)
													return;

												model.bindDetails(result);
												updatePlanetDetailsRelatedElements();
												ViewUtils.unblockUI();
											}

											@Override
											public void onFailure(
													Throwable caught) {
												ViewUtils.unblockUI();
											}
										});
							}
						});
			}
		});

		LinkItem productionButton = new LinkItem("productionButton");
		productionButton.setShowTitle(false);
		productionButton.setLinkTitle("Производить корабли");
		productionButton.setColSpan("*");
		productionButton.setValidators(canControlValidator);
		productionButton
				.setPrompt("<p>Запуск производства кораблей</p><p class=\"description\">Скорость производства - "
						+ Constants.PLANET_ORBIT_UNIT_COUNT_GROW_VELOCITY
						+ " кораблей за цикл. При производстве тратится "
						+ Constants.UNIT_PRICE + " ед. ресурса на корабль</p>");
		productionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (!event.getItem().validate() || destroyed)
					return;

				ViewUtils.blockUI();
				GalaxyServiceProvider.get().startUnitProduction(
						model.getIndex(), new AsyncCallback<PlanetDetails>() {

							@Override
							public void onSuccess(PlanetDetails result) {
								if (destroyed)
									return;

								model.bindDetails(result);
								updatePlanetDetailsRelatedElements();
								ViewUtils.unblockUI();
							}

							@Override
							public void onFailure(Throwable caught) {
								ViewUtils.unblockUI();
							}
						});
			}
		});

		BlurbItem productionProgressbarItem = new BlurbItem(
				"productionProgressbar");
		productionProgressbarItem.setColSpan("*");
		productionProgressbarItem.setDefaultValue(ViewUtils
				.createProcessHTML(""));
		FormItemIcon stopProductionIcon = new FormItemIcon();
		stopProductionIcon.setSrc("[SKIN]/headerIcons/close.png");
		stopProductionIcon.setWidth(15);
		stopProductionIcon.setHeight(15);
		stopProductionIcon.setShowOver(true);
		stopProductionIcon.setPrompt("<p>Остановка производства кораблей</p>");
		productionProgressbarItem.setValidators(canControlValidator);
		productionProgressbarItem.setIcons(stopProductionIcon);
		productionProgressbarItem.addIconClickHandler(new IconClickHandler() {

			@Override
			public void onIconClick(IconClickEvent event) {
				if (!event.getItem().validate() || destroyed)
					return;

				ViewUtils.blockUI();
				GalaxyServiceProvider.get().stopUnitProduction(
						model.getIndex(), new AsyncCallback<PlanetDetails>() {

							@Override
							public void onSuccess(PlanetDetails result) {
								if (destroyed)
									return;

								model.bindDetails(result);
								updatePlanetDetailsRelatedElements();
								ViewUtils.unblockUI();
							}

							@Override
							public void onFailure(Throwable caught) {
								ViewUtils.unblockUI();
							}
						});
			}
		});

		Validator hasResourceForDefence = new CustomValidator() {

			@Override
			protected boolean condition(Object value) {
				return (model.getResourceCount() > (Constants.PLANET_DEFENCE_SWITCH_ON_PRICE + Constants.PLANET_DEFENCE_PRICE));
			}
		};
		hasResourceForDefence.setErrorMessage("Недостаточно ресурсов");

		LinkItem defenceButton = new LinkItem("defenceButton");
		defenceButton.setShowTitle(false);
		defenceButton.setLinkTitle("Включить защиту планеты");
		defenceButton.setColSpan("*");
		defenceButton.setValidators(canControlValidator, hasResourceForDefence);
		defenceButton
				.setPrompt("<p>Активация защиты планеты</p><p class=\"description\">На активацию защиты требуется "
						+ Constants.PLANET_DEFENCE_SWITCH_ON_PRICE
						+ " ед. ресурса. На поддержание - "
						+ Constants.PLANET_DEFENCE_PRICE
						+ " ед. ресурса за цикл</p>");
		defenceButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (!event.getItem().validate() || destroyed)
					return;

				ViewUtils.blockUI();
				GalaxyServiceProvider.get().enableDefence(model.getIndex(),
						new AsyncCallback<PlanetDetails>() {

							@Override
							public void onSuccess(PlanetDetails result) {
								if (destroyed)
									return;

								model.bindDetails(result);
								updatePlanetDetailsRelatedElements();
								ViewUtils.unblockUI();
							}

							@Override
							public void onFailure(Throwable caught) {
								ViewUtils.unblockUI();
							}
						});
			}
		});

		BlurbItem defenceProgressbarItem = new BlurbItem("defenceProgressbar");
		defenceProgressbarItem.setColSpan("*");
		defenceProgressbarItem.setDefaultValue(ViewUtils.createProcessHTML(""));
		FormItemIcon disableDefenceIcon = new FormItemIcon();
		disableDefenceIcon.setSrc("[SKIN]/headerIcons/close.png");
		disableDefenceIcon.setWidth(15);
		disableDefenceIcon.setHeight(15);
		disableDefenceIcon.setShowOver(true);
		disableDefenceIcon.setPrompt("<p>Выключить защиту планеты</p>");
		defenceProgressbarItem.setValidators(canControlValidator);
		defenceProgressbarItem.setIcons(disableDefenceIcon);
		defenceProgressbarItem.addIconClickHandler(new IconClickHandler() {

			@Override
			public void onIconClick(IconClickEvent event) {
				if (!event.getItem().validate() || destroyed)
					return;

				ViewUtils.blockUI();
				GalaxyServiceProvider.get().disableDefence(model.getIndex(),
						new AsyncCallback<PlanetDetails>() {

							@Override
							public void onSuccess(PlanetDetails result) {
								if (destroyed)
									return;

								model.bindDetails(result);
								updatePlanetDetailsRelatedElements();
								ViewUtils.unblockUI();
							}

							@Override
							public void onFailure(Throwable caught) {
								ViewUtils.unblockUI();
							}
						});
			}
		});

		Validator hasFleetForTransportation = new CustomValidator() {

			@Override
			protected boolean condition(Object value) {
				return (model.getOrbitUnitCount() > 0);
			}
		};
		hasFleetForTransportation.setErrorMessage("Нет кораблей");

		LinkItem fleetTransButton = new LinkItem("fleetTransButton");
		fleetTransButton.setShowTitle(false);
		fleetTransButton.setLinkTitle("Перебросить корабли");
		fleetTransButton.setColSpan("*");
		fleetTransButton.setValidators(canControlValidator,
				hasFleetForTransportation);
		fleetTransButton
				.setPrompt("<p>Подготовка кораблей к переброске на другую планету</p><ol><li class=\"description\">Нажмите сюда</li><li class=\"description\">Выберите целевую планету (в любом секторе)</li><li class=\"description\">Выберите количество кораблей и подтвердите переброску в форме, расположенной ниже</li></ol>");
		fleetTransButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!event.getItem().validate())
					return;

				getGalaxyView().getGlobalTransFormView()
						.startTargetPlanetSelection(
								GlobalTransFormView.TRANS_TYPE_FLEET, model);
				ViewUtils.attractAttention(getGalaxyView()
						.getGlobalTransFormView().getHtmlFlow());
			}
		});

		Validator hasResourceForTransportation = new CustomValidator() {

			@Override
			protected boolean condition(Object value) {
				return (model.getResourceCount() > Constants.EPS);
			}
		};
		hasResourceForTransportation.setErrorMessage("Нет ресурсов");

		LinkItem resTransButton = new LinkItem("resTransButton");
		resTransButton.setShowTitle(false);
		resTransButton.setLinkTitle("Перебросить ресурсы");
		resTransButton.setColSpan("*");
		resTransButton.setValidators(canControlValidator,
				hasResourceForTransportation);
		resTransButton
				.setPrompt("<p>Подготовка ресурсов к переброске на другую планету</p><ol><li class=\"description\">Нажмите сюда</li><li class=\"description\">Выберите целевую планету (в любом секторе)</li><li class=\"description\">Выберите количество ресурсов и подтвердите переброску в форме, расположенной ниже</li></ol>");
		resTransButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!event.getItem().validate())
					return;

				getGalaxyView().getGlobalTransFormView()
						.startTargetPlanetSelection(
								GlobalTransFormView.TRANS_TYPE_RESOURCE, model);
				ViewUtils.attractAttention(getGalaxyView()
						.getGlobalTransFormView().getHtmlFlow());
			}
		});

		form.setFields(nameField, ownerField, paramsSection,
				resourceCountField, resourceCountLimitField,
				orbitUnitCountField, orbitUnitCountLimitField,
				currentDefenceKField, defenceKField, actionsSection,
				homeButton, defenceButton, defenceProgressbarItem,
				productionButton, productionProgressbarItem, fleetTransButton,
				resTransButton);

		getGalaxyView().getPlanetSection().getItems()[0].addChild(form);

		form.hide();
	}

	private void createPlanetDetailsDiv() {
		planetDetailsDiv = Document.get().createElement("div");
		planetDetailsDiv.setAttribute("style", "display: none");
		Document.get().getElementById("scene").appendChild(planetDetailsDiv);
	}

	private void requestPlanetRename(String newName) {
		if (destroyed)
			return;

		ViewUtils.blockUI();
		GalaxyServiceProvider.get().renamePlanet(model.getIndex(), newName,
				new AsyncCallback<PlanetDetails>() {

					@Override
					public void onSuccess(PlanetDetails result) {
						if (destroyed)
							return;

						model.bindDetails(result);
						updatePlanetDetailsRelatedElements();
						ViewUtils.unblockUI();
					}

					@Override
					public void onFailure(Throwable caught) {
						ViewUtils.unblockUI();
					}
				});
	}

	@Override
	protected void onResize() {
		super.onResize();
		updatePlanetDetailsDiv();
	}

	public void updatePlanetDetailsRelatedElements() {
		listGridRecord.setAttribute("model", model);
		listGridRecord.setAttribute("index", "" + model.getIndex());
		listGridRecord.setAttribute("planetName", model.getPlanetName());
		listGridRecord.setAttribute("text", model.getText());
		listGridRecord.setAttribute("ownerId", model.getOwnerId());
		listGridRecord.setAttribute("ownerName", model.getOwnerName());
		listGridRecord.setAttribute("resourceCount",
				Math.round(model.getResourceCount() * 100) / 100.0);
		listGridRecord.setAttribute("resourceCountLimit",
				Math.round(model.getResourceCountLimit() * 100) / 100.0);
		listGridRecord
				.setAttribute("orbitUnitCount", model.getOrbitUnitCount());
		listGridRecord.setAttribute("orbitUnitCountLimit",
				model.getOrbitUnitCountLimit());
		if (model.isDefenceEnabled()) {
			listGridRecord.setAttribute("currentDefenceK",
					Math.round(model.getDefenceK() * 100) / 100.0);
		} else {
			listGridRecord.setAttribute("currentDefenceK", 1.0);
		}
		listGridRecord.setAttribute("defenceK",
				Math.round(model.getDefenceK() * 100) / 100.0);

		if (getSectorView().getSectorPlanetsGrid() != null) {
			getSectorView().getSectorPlanetsGrid().markForRedraw();
		}
		getSectorView().updateGoHomeButton();
		if ((getSectorView().getActivePlanetView() == getPlanetView())
				&& (getGalaxyView().getPlanetSection() != null)) {
			String sectionTitle = ("Планета " + model.getText());
			if (model.isHome()) {
				sectionTitle += " [база]";
			}
			getGalaxyView().getPlanetSection().setTitle(sectionTitle);
		}

		boolean planetOwned = UserContainer.get().getUserId()
				.equals(model.getOwnerId());

		form.getField("ownerName").setDisabled(planetOwned);
		form.getField("ownerName").setShowDisabled(false);

		form.getField("planetName").show();
		form.getField("planetName").setDisabled(!planetOwned);
		form.getField("planetName").setShowDisabled(false);

		form.getField("homeButton").hide();
		form.getField("productionButton").hide();
		form.getField("defenceButton").hide();
		form.getField("resTransButton").hide();
		form.getField("fleetTransButton").hide();

		boolean showActionsSectionHeader = false;

		if ((UserContainer.get().getHomePlanetDetails() == null)
				&& (model.getOwnerId() == null)) {
			form.getField("homeButton").show();
			showActionsSectionHeader = true;
		}

		FormItem productionProgressbarItem = form
				.getField("productionProgressbar");
		productionProgressbarItem.setDisabled(!planetOwned);
		if (model.isUnitProduction()) {
			productionProgressbarItem.show();
			showActionsSectionHeader = true;
			if (model.getResourceCount() < Constants.UNIT_PRICE) {
				productionProgressbarItem
						.setDefaultValue(ViewUtils
								.createProcessHTML("Производство кораблей [ожидание ресурсов]"));
			} else if (model.getOrbitUnitCount() >= model
					.getOrbitUnitCountLimit()) {
				productionProgressbarItem
						.setDefaultValue(ViewUtils
								.createProcessHTML("Производство кораблей [достигнут лимит]"));
			} else {
				productionProgressbarItem.setDefaultValue(ViewUtils
						.createProcessHTML("Производство кораблей ["
								+ model.getOrbitUnitCount() + "/"
								+ model.getOrbitUnitCountLimit() + "]"));
			}
		} else {
			productionProgressbarItem.hide();
			if (planetOwned) {
				form.getField("productionButton").show();
				showActionsSectionHeader = true;
			}
		}

		FormItem defenceProgressbarItem = form.getField("defenceProgressbar");
		defenceProgressbarItem.setDisabled(!planetOwned);
		if (model.isDefenceEnabled()) {
			defenceProgressbarItem.show();
			showActionsSectionHeader = true;
			defenceProgressbarItem.setDefaultValue(ViewUtils
					.createProcessHTML("Система защиты планеты [активна]"));
		} else {
			defenceProgressbarItem.hide();
			if (planetOwned) {
				form.getField("defenceButton").show();
				showActionsSectionHeader = true;
			}
		}

		if (planetOwned) {
			form.getField("fleetTransButton").show();
			form.getField("resTransButton").show();
			showActionsSectionHeader = true;
		}

		if (showActionsSectionHeader) {
			form.getField("actionsSection").show();
		} else {
			form.getField("actionsSection").hide();
		}

		form.reset();
		form.editRecord(listGridRecord);

		updatePlanetDetailsDiv();
		updateTransportationsViews();
	}

	void updatePlanetDetailsDiv() {
		Canvas sectorCenterPanel = getGalaxyView().getSectorCenterPanel();
		int w = sectorCenterPanel.getWidth();
		int h = sectorCenterPanel.getHeight();
		if ((w == 0) || (h == 0)) {
			return;
		}

		updatePlanetDetailsDivContent();

		int e = Math.min(w, h);

		double sl = Constants.SECTOR_LINEAR_SIZE;
		double slv = (sl * ViewConstants.SECTOR_VIEW_RELATIVE_SIZE);

		double x = ((slv - sl) / 2 + model.getRelativeX()) / slv;
		double y = ((slv - sl) / 2 + ((sl - model.getRelativeY())
				- model.getRadius() - slv / 50))
				/ slv;

		int left = ((w - e) / 2 + (int) (x * e) - planetDetailsDiv
				.getClientWidth() / 2);
		int top = ((h - e) / 2 + (int) (y * e) - planetDetailsDiv
				.getClientHeight());

		planetDetailsDiv.setAttribute("style", "position: absolute; left:"
				+ left + "px; top:" + top + "px; padding: 0; margin: 0;");
	}

	private void updateTransportationsViews() {
		if (getSectorView().getTransportationViews() != null) {
			for (TransportationView transportationView : getSectorView()
					.getTransportationViews().values()) {
				Transportation transportation = transportationView.getModel();
				if ((model.getIndex() == transportation.getSourceCellIndex())
						|| (model.getIndex() == transportation
								.getTargetCellIndex())) {
					transportationView.updateTransportationRelatedElements();
				}
			}
		}
	}

	private void updatePlanetDetailsDivContent() {
		String html = "";
		String color;
		String fontSize;
		String backColor;
		if (UserContainer.get().getUserId().equals(model.getOwnerId())) {
			color = ("rgb(" + ViewConstants.OWN_CSS_COL_R + ","
					+ ViewConstants.OWN_CSS_COL_G + ","
					+ ViewConstants.OWN_CSS_COL_B + ")");
			backColor = ("rgb(" + ViewConstants.OWN_CSS_COL_R / 2 + ","
					+ ViewConstants.OWN_CSS_COL_G / 2 + ","
					+ ViewConstants.OWN_CSS_COL_B / 2 + ")");
			fontSize = "10px";
		} else if (model.getOwnerId() == null) {
			color = ("rgb(" + ViewConstants.NATIVE_CSS_COL_R + ","
					+ ViewConstants.NATIVE_CSS_COL_G + ","
					+ ViewConstants.NATIVE_CSS_COL_B + ")");
			backColor = ("rgb(" + ViewConstants.NATIVE_CSS_COL_R / 2 + ","
					+ ViewConstants.NATIVE_CSS_COL_G / 2 + ","
					+ ViewConstants.NATIVE_CSS_COL_B / 2 + ")");
			fontSize = "10px";
		} else {
			color = ("rgb(" + ViewConstants.ENEMY_CSS_COL_R + ","
					+ ViewConstants.ENEMY_CSS_COL_G + ","
					+ ViewConstants.ENEMY_CSS_COL_B + ")");
			backColor = ("rgb(" + ViewConstants.ENEMY_CSS_COL_R / 2 + ","
					+ ViewConstants.ENEMY_CSS_COL_G / 2 + ","
					+ ViewConstants.ENEMY_CSS_COL_B / 2 + ")");
			fontSize = "10px";
		}
		if (model.getPlanetName() != null) {
			html += "<div style=\"padding: 0; margin: 0; text-align: center;\">"
					+ "<span style=\"font-size: "
					+ fontSize
					+ "; color: "
					+ color + ";\">" + model.getPlanetName() + "</span></div>";
		}
		int planetLife = (model.getOrbitUnitCount() * 100 / model
				.getOrbitUnitCountLimit());
		html += "<div title=\"Корабли "
				+ planetLife
				+ "%\" style=\"position: relative; padding: 0; margin: 0; width: 50px;"
				+ "height: 2px; background-color: " + backColor
				+ "\"><div style=\""
				+ "padding: 0; margin: 0; height: 100%; width: " + planetLife
				+ "%; background-color: " + color
				+ "; border-right: 1px solid black;\"/></div>";
		planetDetailsDiv.setInnerHTML(html);

		int delta = (planetDetailsDiv.getClientWidth() - 50);
		if (delta > 2) {
			delta /= 2;
			Element planetLifeDiv = (Element) planetDetailsDiv.getLastChild();
			planetLifeDiv.setAttribute("style",
					planetLifeDiv.getAttribute("style") + "; left: " + delta
							+ "px;");
		}
	}

	public void showPlanetDetailsDiv() {
		if (planetDetailsDiv != null) {
			planetDetailsDiv.setClassName("visible");
		}
	}

	public void hidePlanetDetailsDiv() {
		if (planetDetailsDiv != null) {
			planetDetailsDiv.setClassName("invisible");
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		destroyPlanetDetailsDiv();
		getGalaxyView().getPlanetSection().getItems()[0].removeChild(form);
	}

	private void destroyPlanetDetailsDiv() {
		if (planetDetailsDiv != null) {
			planetDetailsDiv.removeFromParent();
			planetDetailsDiv = null;
		}
	}

}
