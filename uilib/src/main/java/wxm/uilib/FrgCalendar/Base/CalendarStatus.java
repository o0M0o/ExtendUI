package wxm.uilib.FrgCalendar.Base;

/**
 * @author WangXM
 * @version create：2018/5/2
 */
public enum CalendarStatus {
    // when ListView been push to Top
    LIST_OPEN,
    // when ListView stay original position
    LIST_CLOSE,
    // when VIEW is dragging
    DRAGGING,
    //when dragging end,the both CalendarView and ListView will animate to specify position.
    ANIMATING,
}
