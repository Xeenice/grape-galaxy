package org.grape.galaxy.client;

import org.grape.galaxy.client.service.FeedbackServiceProvider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class FeedbackView {

	private static final int SUBJECT_LENGTH = 128;
	private static final int COMMENTS_LENGTH = 4096;

	private Window feedbackWnd;

	public FeedbackView() {
		feedbackWnd = new Window();
		feedbackWnd.setWidth(400);
		feedbackWnd.setHeight(350);
		feedbackWnd.setTitle("Форма жалоб и предложений");
		feedbackWnd.setShowMinimizeButton(false);
		feedbackWnd.setIsModal(true);
		feedbackWnd.setShowModalMask(true);
		feedbackWnd.centerInPage();
		feedbackWnd.setCanDragResize(true);
		feedbackWnd.addCloseClickHandler(new CloseClickHandler() {

			public void onCloseClick(CloseClientEvent event) {
				feedbackWnd.hide();
			}

		});

		final DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setOverflow(Overflow.HIDDEN);

		final TextItem subjectItem = new TextItem();
		subjectItem.setShowTitle(false);
		subjectItem.setColSpan("*");
		subjectItem.setWidth("*");
		subjectItem.setHint("[Тема]");
		subjectItem.setShowHintInField(true);
		subjectItem.setRequired(true);
		subjectItem.setRequiredMessage("Пожалуйста, напишите тему сообщения");
		subjectItem.setLength(SUBJECT_LENGTH);
		subjectItem.setBrowserSpellCheck(true);
		subjectItem.setValidateOnChange(true);

		final TextAreaItem commentsItem = new TextAreaItem();
		commentsItem.setShowTitle(false);
		commentsItem.setColSpan("*");
		commentsItem.setWidth("*");
		commentsItem.setHeight("*");
		commentsItem.setHint("[Текст сообщения]");
		commentsItem.setShowHintInField(true);
		commentsItem.setRequired(true);
		commentsItem.setRequiredMessage("Пожалуйста, напишите текст сообщения");
		commentsItem.setLength(COMMENTS_LENGTH);
		commentsItem.setBrowserSpellCheck(true);
		commentsItem.setValidateOnChange(true);

		ButtonItem buttonItem = new ButtonItem("send", "Отправить");
		buttonItem.setColSpan("*");
		buttonItem.setAlign(Alignment.RIGHT);
		buttonItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (form.validate()) {
					FeedbackServiceProvider.get().sendMessage(
							subjectItem.getValueAsString(),
							commentsItem.getValue().toString(),
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									SC.say("Спасибо за Ваше сообщение. Приятно, что уделили внимание.");
									subjectItem.clearValue();
									commentsItem.clearValue();
								}

								@Override
								public void onFailure(Throwable caught) {
									SC.warn("Ошибка: " + caught.getLocalizedMessage());
								}
							});
					hide();
				}
			}
		});

		form.setFields(subjectItem, commentsItem, buttonItem);

		feedbackWnd.addItem(form);
	}

	public void show() {
		feedbackWnd.show();
	}

	public void hide() {
		feedbackWnd.hide();
	}
}
