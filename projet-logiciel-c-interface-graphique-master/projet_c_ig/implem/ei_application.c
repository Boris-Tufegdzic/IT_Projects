#include <stdio.h>
#include "ei_application.h"
#include "ei_widgetclass.h"
#include "classes.h"
#include "ei_event.h"
#include "aux_event.h"
#include "ei_widget_attributes.h"
#include "ei_types.h"
#include "aux_event.h"
#include "aux.h"


static ei_widget_t root_widget;
static ei_surface_t root_window;
static ei_surface_t offscreen;
static bool end_loop;

static ei_linked_rect_t* invalid_rects = NULL;

static ei_widgetclass_t frame = {
    "frame",
    &ei_frame_allocfunc,
    &ei_frame_releasefunc,
    &ei_frame_drawfunc,
    &ei_frame_setdefaultsfunc,
    &ei_frame_geomnotifyfunc,
    &ei_frame_handlefunc,
    NULL
};

static ei_widgetclass_t toplevel = {
    "toplevel",
    &ei_toplevel_allocfunc,
    &ei_toplevel_releasefunc,
    &ei_toplevel_drawfunc,
    &ei_toplevel_setdefaultsfunc,
    &ei_toplevel_geomnotifyfunc,
    &ei_toplevel_handlefunc,
    NULL
};

static ei_widgetclass_t button = {
    "button",
    &ei_button_allocfunc,
    &ei_button_releasefunc,
    &ei_button_drawfunc,
    &ei_button_setdefaultsfunc,
    &ei_button_geomnotifyfunc,
    &ei_button_handlefunc,
    NULL
};

void free_invalid_rects(){
	ei_linked_rect_t* curr_rect = invalid_rects;
	ei_linked_rect_t* temp;
	while(curr_rect != NULL){
		temp = curr_rect;
		curr_rect = curr_rect->next;
		free(temp);
	}
	invalid_rects = NULL;
}

void ei_app_create(ei_size_t main_window_size, bool fullscreen)
{
    hw_init();

    // Registration of native widget classes
    ei_widgetclass_register(&frame);
    ei_widgetclass_register(&toplevel);
    ei_widgetclass_register(&button);

    root_window = hw_create_window(main_window_size, fullscreen);
    offscreen = hw_surface_create(root_window, main_window_size, true);

    root_widget = frame.allocfunc();
    frame.setdefaultsfunc(root_widget);
    root_widget->wclass = &frame;

    root_widget->pick_id = 0;
    root_widget->pick_color = (ei_color_t){0, 0, 0, 0};
    root_widget->user_data = NULL;
    root_widget->destructor = NULL;

    root_widget->parent = NULL;
    root_widget->children_head = NULL;
    root_widget->children_tail = NULL;
    root_widget->next_sibling = NULL;

    root_widget->placer_params = NULL;
    root_widget->requested_size = main_window_size;
    root_widget->screen_location.top_left = (ei_point_t){0, 0};
    root_widget->screen_location.size = main_window_size;
    root_widget->content_rect = root_widget->screen_location;
}


void ei_app_free(void)
{
    ei_widget_destroy(root_widget);
    hw_surface_free(offscreen);

    hw_quit();
}


void ei_app_run(void)
{
    end_loop = false;
    bool updated = true;

    bool need_update;

    ei_event_t* event = malloc(sizeof(ei_event_t));

    ei_widget_t clicked_widget = NULL;
    ei_widget_t active_widget = NULL;

    ei_color_t pick_color;


    while (end_loop == false) {


        /** screen update **/
        if (updated) {

        	hw_surface_lock(root_window);
            hw_surface_lock(offscreen);

        	root_widget->wclass->drawfunc(root_widget, root_window, offscreen, NULL);
            updated = false;

            hw_surface_unlock(offscreen);
            hw_surface_unlock(root_window);

            hw_surface_update_rects(root_window, NULL);
        }

        /** waiting for an event **/
        event->type = ei_ev_none;
        hw_event_wait_next(event);

        /** handle events **/
        if (event->type == ei_ev_mouse_buttondown || event->type == ei_ev_mouse_buttonup) {
            hw_surface_lock(offscreen);
            pick_color = get_color(offscreen, event->param.mouse.where);
            hw_surface_unlock(offscreen);
            clicked_widget = get_clicked_widget(pick_color, root_widget);
            clicked_widget->wclass->handlefunc(clicked_widget, event);
            updated = true;
        }
        else if (event->type == ei_ev_exposed){
                updated = true;
        }
        else if (event->type == ei_ev_close) {
        	ei_app_quit_request();
        }

        
        ei_default_handle_func_t handle_func = ei_event_get_default_handle_func();
        if(handle_func != NULL) {
            need_update = handle_func(event);
            if (need_update) updated = true;
        }


        active_widget = ei_event_get_active_widget();
        if (active_widget != NULL){
            need_update = active_widget->wclass->handlefunc(active_widget, event);
            if (need_update) updated = true;
        }

    }
}


void ei_app_invalidate_rect(const ei_rect_t* rect){
	ei_linked_rect_t* new_rect = malloc(sizeof(ei_linked_rect_t));
	*new_rect = (ei_linked_rect_t){*rect, NULL};

	new_rect->next = invalid_rects;
	invalid_rects = new_rect;

}


void ei_app_quit_request(){
    end_loop = true;
}


ei_widget_t ei_app_root_widget(void)
{
    return root_widget;
}


ei_surface_t ei_app_root_surface(void)
{
    return root_window;
}
