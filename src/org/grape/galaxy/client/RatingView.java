package org.grape.galaxy.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

import org.grape.galaxy.client.service.RatingServiceProvider;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.UserRating;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class RatingView {

	private Layout layout;
	private Layout buttonsLayout;
	private ListGrid listGrid;
	private ImgButton firstPageButton;
	private ImgButton prevPageButton;
	private Label pageNumberLabel;
	private ImgButton nextPageButton;
	private ImgButton lastPageButton;
	private int pageIndex;
	private int lastPageIndex;
	private boolean showed;

	public RatingView() {
		listGrid = new ListGrid();
		listGrid.setWidth100();
		listGrid.setHeight100();
		listGrid.setShowAllRecords(true);

		ListGridField placeField = new ListGridField("place", "Место");
		ListGridField nameField = new ListGridField("name", "Имя");
		nameField.setWidth(150);
		ListGridField ratingField = new ListGridField("rating", "Рейтинг");
		ListGridField planetsField = new ListGridField("planets", "Планеты");
		ListGridField unitsField = new ListGridField("units", "Юниты");
		ListGridField resourcesField = new ListGridField("resources", "Ресурсы");
		listGrid.setFields(placeField, nameField, ratingField, planetsField,
				unitsField, resourcesField);
		listGrid.setCanResizeFields(true);

		buttonsLayout = new HLayout();
		buttonsLayout.setAlign(Alignment.CENTER);
		buttonsLayout.setMembersMargin(2);
		buttonsLayout.setHeight(20);

		firstPageButton = new ImgButton();
		firstPageButton.setSize(15);
		firstPageButton.setShowDown(false);
		firstPageButton.setSrc("[SKIN]/headerIcons/double_arrow_left.png");
		firstPageButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestFirstPage();
			}
		});
		prevPageButton = new ImgButton();
		prevPageButton.setSize(15);
		prevPageButton.setShowDown(false);
		prevPageButton.setSrc("[SKIN]/headerIcons/arrow_left.png");
		prevPageButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestPrevPage();
			}
		});
		pageNumberLabel = new Label();
		pageNumberLabel.setWidth(15);
		pageNumberLabel.setHeight(15);
		pageNumberLabel.setAlign(Alignment.CENTER);
		pageNumberLabel.setBorder("1px solid gray");
		nextPageButton = new ImgButton();
		nextPageButton.setSize(15);
		nextPageButton.setShowDown(false);
		nextPageButton.setSrc("[SKIN]/headerIcons/arrow_right.png");
		nextPageButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestNextPage();
			}
		});
		lastPageButton = new ImgButton();
		lastPageButton.setSize(15);
		lastPageButton.setShowDown(false);
		lastPageButton.setSrc("[SKIN]/headerIcons/double_arrow_right.png");
		lastPageButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestLastPage();
			}
		});
		buttonsLayout.addMember(firstPageButton);
		buttonsLayout.addMember(prevPageButton);
		buttonsLayout.addMember(pageNumberLabel);
		buttonsLayout.addMember(nextPageButton);
		buttonsLayout.addMember(lastPageButton);

		layout = new VLayout();
		layout.setWidth(700);
		layout.setHeight100();
		layout.setLayoutMargin(5);
		layout.setMembersMargin(5);
		layout.addMember(listGrid);
		layout.addMember(buttonsLayout);
	}

	public Canvas getPane() {
		return layout;
	}

	public void notifyShowed() {
		if (!showed) {
			requestCurrentPage();
			showed = true;
		}
	}

	private void requestCurrentPage() {
		ViewUtils.blockUI();
		RatingServiceProvider.get().getRatingPage(pageIndex,
				new AsyncCallback<ArrayList<UserRating>>() {

					@Override
					public void onSuccess(ArrayList<UserRating> result) {
						setData(result);
						updateGUI();
						ViewUtils.unblockUI();
					}

					@Override
					public void onFailure(Throwable caught) {
						setData(null);
						updateGUI();
						ViewUtils.unblockUI();
					}
				});
	}

	private void requestFirstPage() {
		if (pageIndex != 0) {
			pageIndex = 0;
			requestCurrentPage();
		}
	}

	private void requestPrevPage() {
		if (pageIndex > 0) {
			pageIndex--;
			requestCurrentPage();
		}
	}

	private void requestNextPage() {
		if (pageIndex < lastPageIndex) {
			pageIndex++;
			requestCurrentPage();
		}
	}

	private void requestLastPage() {
		if (pageIndex != lastPageIndex) {
			pageIndex = lastPageIndex;
			requestCurrentPage();
		}
	}

	private void setData(ArrayList<UserRating> ratings) {
		listGrid.setData(new Record[0]);
		if ((ratings == null) || ratings.isEmpty()) {
			return;
		}

		lastPageIndex = ratings.get(0).getLastPageIndex();

		int place = pageIndex * Constants.USER_RATING_PAGE_SIZE + 1;
		Collections.sort(ratings);
		for (ListIterator<UserRating> iterator = ratings.listIterator(ratings
				.size()); iterator.hasPrevious();) {
			UserRating userRating = (UserRating) iterator.previous();
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("place", place);
			record.setAttribute("name", userRating.getUserName());
			record.setAttribute("rating", userRating.getRating());
			record.setAttribute("planets", userRating.getPlanetCount());
			record.setAttribute("units", userRating.getOrbitUnitCount());
			record.setAttribute("resources",
					Math.round(userRating.getResourceCount() * 100) / 100.0);
			listGrid.addData(record);
			place++;
		}
	}

	private void updateGUI() {
		if (lastPageIndex > 0) {
			if (pageIndex > 0) {
				firstPageButton.enable();
				prevPageButton.enable();
			} else {
				firstPageButton.disable();
				prevPageButton.disable();
			}
			pageNumberLabel.setContents("" + (pageIndex + 1));
			if (pageIndex < lastPageIndex) {
				nextPageButton.enable();
				lastPageButton.enable();
			} else {
				nextPageButton.disable();
				lastPageButton.disable();
			}
			buttonsLayout.show();
		} else {
			buttonsLayout.hide();
		}
	}

}
