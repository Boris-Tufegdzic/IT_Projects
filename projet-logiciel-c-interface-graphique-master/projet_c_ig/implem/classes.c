#include "ei_types.h"
#include "ei_widget_configure.h"
#include "classes.h"
#include "aux_classes.h"
#include "aux_ei_draw.h"
#include "ei_event.h"
#include "aux_classes.h"
#include "ei_application.h"
#include "aux_event.h"
#include "position.h"


#define OFFSET 5


void widget_draw_children      (ei_widget_t		widget,
                                ei_surface_t		surface,
                                ei_surface_t		pick_surface,
                                ei_rect_t*		clipper)
{
    if(widget != NULL) {
        // Depth first
        ei_widget_t child = widget->children_head;
        while(child != NULL) {
            if(child->wclass->drawfunc != NULL) {
                child->wclass->drawfunc(child, surface, pick_surface, clipper);
            }
            child = child->next_sibling;
        }
    }
}

/** Allocation functions **/

ei_widget_t ei_toplevel_allocfunc()
{
    ei_toplevel_t new_toplevel = calloc(1, sizeof(ei_impl_toplevel_t));
    return (ei_widget_t)new_toplevel;
}

ei_widget_t ei_frame_allocfunc()
{
    ei_frame_t new_frame = calloc(1, sizeof(ei_impl_frame_t));
    return (ei_widget_t)new_frame;
}

ei_widget_t ei_button_allocfunc()
{
    ei_button_t new_button = calloc(1, sizeof(ei_impl_button_t));
    return (ei_widget_t)new_button;
}

/** Release functions **/

void ei_toplevel_releasefunc(ei_widget_t toplevel)
{
    ei_toplevel_t toplevel_casted = (ei_toplevel_t)toplevel;
    if(toplevel_casted->title != NULL) free(toplevel_casted->title);
}

void ei_frame_releasefunc(ei_widget_t frame)
{
    ei_frame_t frame_casted = (ei_frame_t)frame;
    if(frame_casted->text != NULL) free(frame_casted->text);
}

void ei_button_releasefunc(ei_widget_t button)
{
    ei_button_t button_casted = (ei_button_t)button;
    if(button_casted->text != NULL) free(button_casted->text);
}

/** Draw function **/




// Draw functions
void ei_toplevel_drawfunc		(ei_widget_t		toplevel,
							 ei_surface_t		surface,
							 ei_surface_t		pick_surface,
							 ei_rect_t*		clipper)
{
    if(toplevel != NULL) {

        ei_toplevel_t toplevel_casted = (ei_toplevel_t)toplevel;

        ei_color_t darker_color = (ei_color_t){toplevel_casted->color.red*0.5, toplevel_casted->color.green*0.5,toplevel_casted->color.blue*0.5, toplevel_casted->color.alpha};
        int offset = (toplevel_casted->border_width == 0) ? OFFSET : toplevel_casted->border_width;

        // Draw background
        draw_rectangle(surface, toplevel_casted->widget.screen_location, toplevel_casted->color, clipper);
        draw_rectangle(pick_surface, toplevel_casted->widget.screen_location, toplevel_casted->widget.pick_color, clipper);

        // Draw header
        int height;
	    hw_text_compute_size(toplevel_casted->title, ei_default_font, NULL, &height);
	    draw_rectangle(
            surface,
            (ei_rect_t){
                toplevel_casted->widget.screen_location.top_left,
                (ei_size_t){toplevel_casted->widget.screen_location.size.width, height + 2*offset}
            },
            darker_color,
            clipper
        );

        // Draw title
        ei_point_t title_position;

        if(toplevel_casted->closable) {
            title_position = (ei_point_t){
                toplevel_casted->widget.screen_location.top_left.x + 2*offset + height, toplevel_casted->widget.screen_location.top_left.y + offset
            };
        }
        else {
            title_position = (ei_point_t){
                toplevel_casted->widget.screen_location.top_left.x + 2*offset, toplevel_casted->widget.screen_location.top_left.y + offset
            };
        }
        ei_draw_text(surface, &title_position, toplevel_casted->title, ei_default_font, toplevel_casted->color, clipper);


        widget_draw_children(toplevel, surface, pick_surface, &(toplevel->content_rect));


        // Draw borders
        // Left
        ei_rect_t left_rect = (ei_rect_t){
            toplevel_casted->widget.screen_location.top_left,
            (ei_size_t){toplevel_casted->border_width, toplevel_casted->widget.screen_location.size.height}
        };
        draw_rectangle(surface, left_rect, darker_color, clipper);
        draw_rectangle(pick_surface, left_rect, toplevel->pick_color, clipper);

        // Right
        ei_rect_t right_rect = (ei_rect_t){
            (ei_point_t){
                toplevel_casted->widget.screen_location.top_left.x + toplevel_casted->widget.screen_location.size.width - toplevel_casted->border_width,
                toplevel_casted->widget.screen_location.top_left.y
            },
            (ei_size_t){toplevel_casted->border_width, toplevel_casted->widget.screen_location.size.height}
        };
        draw_rectangle(surface, right_rect, darker_color, clipper);
        draw_rectangle(pick_surface, right_rect, toplevel->pick_color, clipper);

        // Bottom
        ei_rect_t bot_rect = (ei_rect_t){
            (ei_point_t){
                toplevel_casted->widget.screen_location.top_left.x,
                toplevel_casted->widget.screen_location.top_left.y + toplevel_casted->widget.screen_location.size.height - toplevel_casted->border_width
            },
            (ei_size_t){toplevel_casted->widget.screen_location.size.width, toplevel_casted->border_width}
        };
        draw_rectangle(surface, bot_rect, darker_color, clipper);
        draw_rectangle(pick_surface, bot_rect, toplevel->pick_color, clipper);

        // Draw bottom-right square (to resize window) -> side of length 3*border_width
        ei_rect_t square = (ei_rect_t){
            (ei_point_t){
                toplevel_casted->widget.screen_location.top_left.x + toplevel_casted->widget.screen_location.size.width - 3*toplevel_casted->border_width,
                toplevel_casted->widget.screen_location.top_left.y + toplevel_casted->widget.screen_location.size.height - 3*toplevel_casted->border_width
            },
            (ei_size_t){3*toplevel_casted->border_width, 3*toplevel_casted->border_width}
        };
        draw_rectangle(surface, square, darker_color, clipper);
        draw_rectangle(pick_surface, square, toplevel->pick_color, clipper);
    } 
}

void ei_frame_drawfunc(ei_widget_t frame,
                ei_surface_t surface,
                ei_surface_t pick_surface,
                ei_rect_t* clipper)
{
    if (frame != NULL) {
        ei_frame_t frame_casted = (ei_frame_t)frame;

        if(frame_casted->relief == ei_relief_none) {
		draw_rectangle(surface, frame->screen_location , frame_casted->color, clipper);
		draw_rectangle(pick_surface, frame->screen_location, frame->pick_color, clipper);
        }
        else {
            /* picking surface drawing */
            draw_button(pick_surface, frame_casted->widget.screen_location, 0, frame_casted->border_width, frame->pick_color, clipper, ei_relief_none);
            /* drawing on the real surface */
            draw_button(surface, frame_casted->widget.screen_location, 0, frame_casted->border_width, frame_casted->color, clipper, frame_casted->relief);
        }

        if(frame_casted->text != NULL)
        {
            int width, height;
            hw_text_compute_size(frame_casted->text, frame_casted->text_font, &width, &height);

            ei_point_t anchor = (ei_point_t){
                frame_casted->widget.screen_location.top_left.x + (frame_casted->widget.screen_location.size.width - width)/2,
                frame_casted->widget.screen_location.top_left.y + (frame_casted->widget.screen_location.size.height - height)/2
            };
            
            ei_draw_text(surface, &anchor, frame_casted->text, frame_casted->text_font, frame_casted->text_color, clipper);
        }
        else if(frame_casted->img != NULL)
        {
            hw_surface_lock(frame_casted->img);

            ei_size_t inter = {
                    min(frame_casted->widget.content_rect.size.width, (*frame_casted->img_rect).size.width),
                    min(frame_casted->widget.content_rect.size.height, (*frame_casted->img_rect).size.height)
            };

            ei_rect_t frame_rect = {
                frame_casted->widget.content_rect.top_left,
                inter
            };

            ei_rect_t img_rect = {
                (*frame_casted->img_rect).top_left,
                inter
            };

            ei_copy_surface(surface, &frame_rect, frame_casted->img, &img_rect, true);

            hw_surface_unlock(frame_casted->img);
        }


        widget_draw_children(frame, surface, pick_surface, &(frame->content_rect));
    }
}

void ei_button_drawfunc		(ei_widget_t		button,
							 ei_surface_t		surface,
							 ei_surface_t		pick_surface,
							 ei_rect_t*		clipper)
{
    if (button != NULL) {
        ei_button_t button_casted = (ei_button_t)button;

        if(strcmp(button->parent->wclass->name, "toplevel") == 0) {
            if(((ei_toplevel_t)button->parent)->closable) {
                if(button->parent->children_head == button) {
                    clipper = &button->parent->screen_location;
                }
            } 
        }

        /* picking surface drawing */
        draw_button(pick_surface, button->screen_location, button_casted->corner_radius, 0, button->pick_color, clipper, ei_relief_none);
        /* drawing on the real surface */
        draw_button(surface, button_casted->widget.screen_location, button_casted->corner_radius, button_casted->border_width, button_casted->color, clipper, button_casted->relief);


        if(button_casted->text != NULL) {
            int width, height;
            hw_text_compute_size(button_casted->text, button_casted->text_font, &width, &height);

            ei_point_t anchor = (ei_point_t){
                button_casted->widget.screen_location.top_left.x + (button_casted->widget.screen_location.size.width - width)/2,
                button_casted->widget.screen_location.top_left.y + (button_casted->widget.screen_location.size.height - height)/2
            };
            
            ei_draw_text(surface, &anchor, button_casted->text, button_casted->text_font, button_casted->text_color, clipper);
        }


        if(button_casted->text != NULL) {
            int width, height;
            hw_text_compute_size(button_casted->text, button_casted->text_font, &width, &height);

            ei_point_t anchor = (ei_point_t){
                    button_casted->widget.screen_location.top_left.x + (button_casted->widget.screen_location.size.width - width)/2,
                    button_casted->widget.screen_location.top_left.y + (button_casted->widget.screen_location.size.height - height)/2
            };

            ei_draw_text(surface, &anchor, button_casted->text, button_casted->text_font, button_casted->text_color, clipper);
        }

        else if (button_casted->img != NULL) {
            ei_size_t inter = {

                    min(button_casted->widget.content_rect.size.width, button_casted->img_rect->size.width),
                    min(button_casted->widget.content_rect.size.height, button_casted->img_rect->size.height)

            };

            ei_rect_t button_rect = {
                    button_casted->widget.content_rect.top_left,
                    inter
            };


            hw_surface_lock(button_casted->img);
            ei_copy_surface(surface, &button_rect, button_casted->img, &button->content_rect, true);
            hw_surface_unlock(button_casted->img);
        }

        widget_draw_children(button, surface, pick_surface, &(button->content_rect));
    }
}


void button_close(ei_widget_t widget, struct ei_event_t* event, ei_user_param_t	user_param)
{
    ei_widget_destroy(widget->parent);
}


// Setdefaults functions
void	ei_toplevel_setdefaultsfunc	(ei_widget_t		toplevel)
{
    toplevel->requested_size = (ei_size_t){320, 240};

    ei_toplevel_t toplevel_casted = (ei_toplevel_t)toplevel;
    toplevel_casted->color = ei_default_background_color;
    toplevel_casted->border_width = 4;
    toplevel_casted->title = malloc(sizeof(char)*80);
    strcpy(toplevel_casted->title, "Toplevel");
    toplevel_casted->closable = true;
    toplevel_casted->resizable = ei_axis_both;
    toplevel_casted->min_size = &(ei_size_t){160, 120};

    int text_height;
    hw_text_compute_size(toplevel_casted->title, ei_default_font, NULL, &text_height);

    ei_widget_t close = ei_widget_create("button", toplevel, NULL, NULL);
    ei_button_configure(
        close,
        &(ei_size_t){text_height, text_height},
        &(ei_color_t){255, 0, 0, 255},
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
    );

    ((ei_button_t)close)->callback = &button_close;

    ei_place_xy(close, toplevel_casted->border_width, toplevel_casted->border_width);
}


void ei_frame_setdefaultsfunc(ei_widget_t frame)
{
    frame->requested_size = (ei_size_t){0, 0};

    ei_frame_t frame_casted = (ei_frame_t)frame;
    frame_casted->color = ei_default_background_color;
    frame_casted->border_width = 0;
    frame_casted->relief = ei_relief_none;
    frame_casted->text = malloc(sizeof(char)*80);
    strcpy(frame_casted->text, "");
    frame_casted->text_font = ei_default_font;
    frame_casted->text_color = ei_font_default_color;
    frame_casted->text_anchor = ei_anc_northwest;
    frame_casted->img = NULL;
    frame_casted->img_rect = NULL;
    frame_casted->img_anchor = ei_anc_northwest;
}

void	ei_button_setdefaultsfunc	(ei_widget_t		button)
{
    button->requested_size = (ei_size_t){0, 0};

    ei_button_t button_casted = (ei_button_t)button;
    button_casted->color = ei_default_background_color;
    button_casted->border_width = k_default_button_border_width;
    button_casted->corner_radius = k_default_button_corner_radius;
    button_casted->relief = ei_relief_raised;
    button_casted->text = NULL;
    button_casted->text_font = ei_default_font;
    button_casted->text_color = ei_font_default_color;
    button_casted->text_anchor = ei_anc_northwest;
    button_casted->img = NULL;
    button_casted->img_rect = NULL;
    button_casted->img_anchor = ei_anc_northwest;
    button_casted->callback = NULL;
    button_casted->user_param = NULL;
}


// Geomnotify functions
void	ei_toplevel_geomnotifyfunc	(ei_widget_t		toplevel)
{
    if(toplevel->placer_params != NULL) {
        ei_toplevel_t toplevel_casted = (ei_toplevel_t)toplevel;
        int offset = (toplevel_casted->border_width == 0) ? OFFSET : toplevel_casted->border_width;

        int title_height;
        hw_text_compute_size(toplevel_casted->title, ei_default_font, NULL, &title_height);

        ei_point_t parent_origin = toplevel->parent->content_rect.top_left;
        int parent_content_width = toplevel->parent->content_rect.size.width;
        int parent_content_height = toplevel->parent->content_rect.size.height;

        ei_point_t origin;
        origin.x = parent_origin.x + toplevel->placer_params->rel_x*parent_content_width + toplevel->placer_params->x;
        origin.y = parent_origin.y + toplevel->placer_params->rel_y*parent_content_height + toplevel->placer_params->y;

        int widget_width = toplevel->placer_params->rel_width*parent_content_width + toplevel->placer_params->width;
        int widget_height = toplevel->placer_params->rel_height*parent_content_height + toplevel->placer_params->height;

        switch (toplevel->placer_params->anchor) {
            case ei_anc_center:
                origin.x -= widget_width / 2;
                origin.y -= widget_height / 2;
                break;
            case ei_anc_southeast:
                origin.x -= widget_width;
                origin.y -= widget_height;
                break;
            case ei_anc_southwest:
                origin.y -= widget_height;
                break;
            case ei_anc_northeast:
                origin.x -= widget_width;
                break;
            case ei_anc_north:
                origin.x -= widget_width / 2;
                break;
            case ei_anc_south:
                origin.x -= widget_width / 2;
                origin.y -= widget_height;
                break;
            case ei_anc_west:
                origin.y -= widget_height / 2;
                break;
            case ei_anc_east:
                origin.x -= widget_width;
                origin.y -= widget_height / 2;
                break;
            default:
                break;
        }

        // Screen location
        toplevel->screen_location.top_left = origin;
        toplevel->screen_location.size = (ei_size_t){widget_width + 2*toplevel_casted->border_width, widget_height + toplevel_casted->border_width + 2*offset + title_height};

        // Content_rect (under head and between borders)
        ei_rect_t content_rect = {
            (ei_point_t){origin.x + toplevel_casted->border_width, origin.y + 2*offset + title_height},
            (ei_size_t){widget_width, widget_height}
        };

        toplevel->content_rect = content_rect;
    }
}

void ei_frame_geomnotifyfunc(ei_widget_t frame)
{
    if(frame->placer_params != NULL) {

        ei_frame_t frame_casted = (ei_frame_t)frame;

        ei_point_t parent_origin = frame->parent->content_rect.top_left;
        int parent_content_width = frame->parent->content_rect.size.width;
        int parent_content_height = frame->parent->content_rect.size.height;

        ei_point_t origin;
        origin.x = parent_origin.x + frame->placer_params->rel_x*parent_content_width + frame->placer_params->x;
        origin.y = parent_origin.y + frame->placer_params->rel_y*parent_content_height + frame->placer_params->y;

        int title_width = 0;
        int title_height = 0;
        if(frame_casted->text != NULL) {
            hw_text_compute_size(frame_casted->text, frame_casted->text_font, &title_width, &title_height);
        }

        int widget_width = max(frame->placer_params->rel_width*parent_content_width + frame->placer_params->width, title_width + 4*frame_casted->border_width);
        int widget_height = max(frame->placer_params->rel_height*parent_content_height + frame->placer_params->height, title_height + 4*frame_casted->border_width);

        switch (frame->placer_params->anchor) {
            case ei_anc_center:
                origin.x -= widget_width / 2;
                origin.y -= widget_height / 2;
                break;
            case ei_anc_southeast:
                origin.x -= widget_width;
                origin.y -= widget_height;
                break;
            case ei_anc_southwest:
                origin.y -= widget_height;
                break;
            case ei_anc_northeast:
                origin.x -= widget_width;
                break;
            case ei_anc_north:
                origin.x -= widget_width / 2;
                break;
            case ei_anc_south:
                origin.x -= widget_width / 2;
                origin.y -= widget_height;
                break;
            case ei_anc_west:
                origin.y -= widget_height / 2;
                break;
            case ei_anc_east:
                origin.x -= widget_width;
                origin.y -= widget_height / 2;
                break;
            default:
                break;
        }

        // Screen location
        frame->screen_location.top_left = origin;
        frame->screen_location.size = (ei_size_t){widget_width, widget_height};

        // Content_rect (between borders)
        ei_rect_t content_rect = {
            (ei_point_t){origin.x + frame_casted->border_width, origin.y + frame_casted->border_width},
            (ei_size_t){widget_width - 2*frame_casted->border_width, widget_height - 2*frame_casted->border_width}
        };

        frame->content_rect = content_rect;
    }
}

void	ei_button_geomnotifyfunc	(ei_widget_t		button)
{
    if(button->placer_params != NULL) {

        ei_button_t button_casted = (ei_button_t)button;

        ei_point_t parent_origin = button->parent->content_rect.top_left;
        int parent_content_width = button->parent->content_rect.size.width;
        int parent_content_height = button->parent->content_rect.size.height;

        if(strcmp(button->parent->wclass->name, "toplevel") == 0) {
            if(((ei_toplevel_t)button->parent)->closable) {
                if(button->parent->children_head == button) {
                    parent_origin = button->parent->screen_location.top_left;
                    parent_content_width = button->parent->screen_location.size.width;
                    parent_content_height = button->parent->screen_location.size.height;
                }
            } 
        }

        ei_point_t origin;
        origin.x = parent_origin.x + button->placer_params->rel_x*parent_content_width + button->placer_params->x;
        origin.y = parent_origin.y + button->placer_params->rel_y*parent_content_height + button->placer_params->y;

        int title_width = 0;
        int title_height = 0;
        if(button_casted->text != NULL) {
            hw_text_compute_size(button_casted->text, button_casted->text_font, &title_width, &title_height);
        }

        int widget_width = max(button->placer_params->rel_width*parent_content_width + button->placer_params->width, title_width + 4*button_casted->border_width);
        int widget_height = max(button->placer_params->rel_height*parent_content_height + button->placer_params->height, title_height + 4*button_casted->border_width);

        switch (button->placer_params->anchor) {
            case ei_anc_center:
                origin.x -= widget_width / 2;
                origin.y -= widget_height / 2;
                break;
            case ei_anc_southeast:
                origin.x -= widget_width;
                origin.y -= widget_height;
                break;
            case ei_anc_southwest:
                origin.y -= widget_height;
                break;
            case ei_anc_northeast:
                origin.x -= widget_width;
                break;
            case ei_anc_north:
                origin.x -= widget_width / 2;
                break;
            case ei_anc_south:
                origin.x -= widget_width / 2;
                origin.y -= widget_height;
                break;
            case ei_anc_west:
                origin.y -= widget_height / 2;
                break;
            case ei_anc_east:
                origin.x -= widget_width;
                origin.y -= widget_height / 2;
                break;
            default:
                break;
        }

        // Screen location
        button->screen_location.top_left = origin;
        button->screen_location.size = (ei_size_t){widget_width, widget_height};

        // Content_rect (between borders)
        ei_rect_t content_rect = {
            (ei_point_t){origin.x + button_casted->border_width, origin.y + button_casted->border_width},
            (ei_size_t){widget_width - 2*button_casted->border_width, widget_height - 2*button_casted->border_width}
        };

        button->content_rect = content_rect;
    }
}




// Handle functions
bool	ei_toplevel_handlefunc		(ei_widget_t		toplevel,
						 	 struct ei_event_t*	event)
{
    // size changing
    ei_toplevel_t casted_toplevel = (ei_toplevel_t)toplevel;
    ei_rect_t rect = toplevel->screen_location;
    ei_size_t size = rect.size;
    int border_width = casted_toplevel->border_width;

    int width, height;
    hw_text_compute_size(casted_toplevel->title, ei_default_font, &width, &height);

    // mouse click event
    if (event->type == ei_ev_mouse_buttondown ) {
        ei_point_t click = event->param.mouse.where;

        ei_point_t mouse_pos = {click.x - toplevel->screen_location.top_left.x, click.y - toplevel->screen_location.top_left.y};
        ei_event_set_mouse_position(mouse_pos);

        put_to_last_children(toplevel);

        if (click_in_square(click, rect.top_left, size.width, size.height, border_width)){
            ei_event_set_active_widget(toplevel);
            ei_event_set_active_widget_type(0);
            return true;
        } else if (click_in_header(click, rect.top_left, size.width, height, border_width)) {
            ei_event_set_active_widget(toplevel);
            ei_event_set_active_widget_type(1);
            return true;
        }
    } else if (event->type == ei_ev_mouse_buttonup && ei_event_get_active_widget() == toplevel) {
        ei_event_set_active_widget(NULL);
        return true;
    } else if (event->type == ei_ev_mouse_move){
        if (ei_event_get_active_widget_type() == 0) {

            int min_width = 6*border_width + width;
            min_width += (((ei_toplevel_t)toplevel)->closable) ? height : 0;

            ei_place(
            toplevel,
            &toplevel->placer_params->anchor,
            NULL,
            NULL,
            &(int){max(event->param.mouse.where.x - rect.top_left.x, min_width)},
            &(int){(max(event->param.mouse.where.y - rect.top_left.y, height + 2*border_width))},
            NULL,
            NULL,
            NULL,
            NULL
		    );
            
        } else {
            int x = event->param.mouse.where.x - ei_event_get_mouse_position().x;
            int y = event->param.mouse.where.y - ei_event_get_mouse_position().y;
            ei_place(toplevel, &toplevel->placer_params->anchor,&x, &y, NULL, NULL, NULL, NULL, NULL, NULL);
        }
        return true;
    }
    return false;
}

bool ei_frame_handlefunc(ei_widget_t frame, struct ei_event_t* event)
{
    return false;
}



bool	ei_button_handlefunc		(ei_widget_t		button,
						 	 struct ei_event_t*	event)
{
    ei_button_t casted_button = (ei_button_t)button;
    bool return_value = false;

    if(event->type == ei_ev_mouse_buttondown && ei_event_get_active_widget()!=button) {
        ei_event_set_mouse_in_button_rect(true);

        casted_button->relief = ei_relief_sunken;
        ei_event_set_active_widget(button);
        return_value = true;
    }
    
    if(event->type == ei_ev_mouse_buttonup) {
        if (casted_button->callback != NULL && ei_event_get_mouse_in_button_rect() == true)
            casted_button->callback(button, event, casted_button->user_param);

        casted_button->relief = ei_relief_raised;
        ei_event_set_active_widget(NULL);
        return true;
    }

    if (event->type == ei_ev_mouse_move && ei_event_get_active_widget() == button){
        if (! in_rect(event->param.mouse.where, button->screen_location) && ei_event_get_mouse_in_button_rect() == true){
            casted_button->relief = ei_relief_raised;
            ei_event_set_mouse_in_button_rect(false);
            return true;
        } else if ( in_rect(event->param.mouse.where, button->screen_location) && ei_event_get_mouse_in_button_rect() == false) {
            casted_button->relief = ei_relief_sunken;
            ei_event_set_mouse_in_button_rect(true);
            return true;
        } else {
            return false;
        }
    }
    return return_value;
}
