package ru.practicum.event.enums;

import lombok.ToString;

@ToString
public enum StateAction {
    SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT, NEED_EDITS
}
