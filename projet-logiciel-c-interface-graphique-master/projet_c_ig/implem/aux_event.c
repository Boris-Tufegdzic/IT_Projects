
#include "aux_event.h"
#include "aux.h"
#include "classes.h"
/* active_widget_type :
 *
 * 0 : size changing
 * 1 : position changing
 *
 */
static int active_widget_type;

static ei_point_t mouse_position;

static bool mouse_in_button_rect;


void ei_event_set_active_widget_type(int type){
    active_widget_type = type;
}

int ei_event_get_active_widget_type(void){
    return active_widget_type;
}


void ei_event_set_mouse_position(ei_point_t point){
	mouse_position = point;
}

ei_point_t ei_event_get_mouse_position(void){
	return mouse_position;
}

void ei_event_set_mouse_in_button_rect(bool value){
    mouse_in_button_rect = value;
}

bool ei_event_get_mouse_in_button_rect(void){
    return mouse_in_button_rect;
}

ei_widget_t get_clicked_widget(ei_color_t color, ei_widget_t widget)
{
    if(widget != NULL){
        if(same_color(widget->pick_color, color)){
            return widget;
        } else {
            ei_widget_t child = widget->children_head;
            ei_widget_t returned = NULL;
            while(child != NULL) {
                returned = get_clicked_widget(color, child);
                if (returned!=NULL){
                    return returned;
                } else {
                    child = child->next_sibling;
                }
            }
            return NULL;
        }
    }
    return NULL;
}