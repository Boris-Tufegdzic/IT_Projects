

#ifndef PROJETC_IG_AUX_EVENT_H
#define PROJETC_IG_AUX_EVENT_H

#include "ei_types.h"
#include "ei_widget.h"


/* ei_event_set_active_widget_type :
 *
 * it set the value of active_widget_type to value of type
 *
 */
void ei_event_set_active_widget_type(int type);

/* ei_event_get_active_widget_type :
 *
 * return the value of active_widget_type
 *
 */
int ei_event_get_active_widget_type(void);


void ei_event_set_mouse_position(ei_point_t point);

ei_point_t ei_event_get_mouse_position(void);


void ei_event_set_mouse_in_button_rect(bool value);

bool ei_event_get_mouse_in_button_rect(void);
/*  get_clicked_widget :
 *
 *  return true if color1 and color2 are the same colors
 *
 *  */
ei_widget_t get_clicked_widget(ei_color_t color, ei_widget_t widget);
#endif //PROJETC_IG_AUX_EVENT_H
