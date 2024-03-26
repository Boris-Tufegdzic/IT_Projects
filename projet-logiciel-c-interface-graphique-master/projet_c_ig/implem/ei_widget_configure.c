#include "ei_widgetclass.h"
#include "classes.h"
#include "ei_event.h"


void ei_frame_configure(ei_widget_t		frame,
						ei_size_t*		requested_size,
						const ei_color_t*	color,
						int*			border_width,
						ei_relief_t*		relief,
						ei_string_t*		text,
						ei_font_t*		text_font,
						ei_color_t*		text_color,
						ei_anchor_t*		text_anchor,
						ei_surface_t*		img,
						ei_rect_ptr_t*		img_rect,
						ei_anchor_t*		img_anchor)
{
    ei_frame_t frame_casted = (ei_frame_t)frame;

    // Configure attributes
    if(requested_size != NULL) frame_casted->widget.requested_size = *requested_size;
    if(color != NULL) frame_casted->color = *color;
    if(border_width != NULL) frame_casted->border_width = *border_width;
    if(relief != NULL) frame_casted->relief = *relief;

    if(text != NULL) {
        if(frame_casted->text == NULL) {
            frame_casted->text = malloc(sizeof(char)*80);
            strcpy(frame_casted->text, *text);
        }
        else {
            strcpy(frame_casted->text, *text);
        }
    }

    if(text_font != NULL) frame_casted->text_font = *text_font;
    if(text_color != NULL) frame_casted->text_color = *text_color;
    if(text_anchor != NULL) frame_casted->text_anchor = *text_anchor;
    if(img != NULL) frame_casted->img = *img;
    if(img_rect != NULL) frame_casted->img_rect = *img_rect;
    if(img_anchor != NULL) frame_casted->img_anchor = *img_anchor;
}


void			ei_button_configure		(ei_widget_t		button,
							 ei_size_t*		requested_size,
							 const ei_color_t*	color,
							 int*			border_width,
							 int*			corner_radius,
							 ei_relief_t*		relief,
							 ei_string_t*		text,
							 ei_font_t*		text_font,
							 ei_color_t*		text_color,
							 ei_anchor_t*		text_anchor,
							 ei_surface_t*		img,
							 ei_rect_ptr_t*		img_rect,
							 ei_anchor_t*		img_anchor,
							 ei_callback_t*		callback,
							 ei_user_param_t*	user_param)
{
    ei_button_t button_casted = (ei_button_t)button;

    // Configure attributes
    if(requested_size != NULL) button_casted->widget.requested_size = *requested_size;
    if(color != NULL) button_casted->color = *color;
    if(border_width != NULL) button_casted->border_width = *border_width;
    if(corner_radius != NULL) button_casted->corner_radius = *corner_radius;
    if(relief != NULL) button_casted->relief = *relief;

    if(text != NULL) {
        if(button_casted->text == NULL) {
            button_casted->text = malloc(sizeof(char)*80);
            strcpy(button_casted->text, *text);
        }
        else {
            strcpy(button_casted->text, *text);
        }
    }

    if(text_font != NULL) button_casted->text_font = *text_font;
    if(text_color != NULL) button_casted->text_color = *text_color;
    if(text_anchor != NULL) button_casted->text_anchor = *text_anchor;
    if(img != NULL) button_casted->img = *img;
    if(img_rect != NULL) button_casted->img_rect = *img_rect;
    if(img_anchor != NULL) button_casted->img_anchor = *img_anchor;
    if(callback != NULL) button_casted->callback = *callback;
    if(user_param != NULL) button_casted->user_param = *user_param;
}


void			ei_toplevel_configure		(ei_widget_t	toplevel,
							 ei_size_t*		requested_size,
							 ei_color_t*		color,
							 int*			border_width,
							 ei_string_t*		title,
							 bool*			closable,
							 ei_axis_set_t*		resizable,
						 	 ei_size_ptr_t*		min_size)
{
    ei_toplevel_t toplevel_casted = (ei_toplevel_t)toplevel;
    
    // Configure attributes
    if(requested_size != NULL) toplevel_casted->widget.requested_size = *requested_size;
    if(color != NULL) toplevel_casted->color = *color;
    if(border_width != NULL) toplevel_casted->border_width = *border_width;

    if(title != NULL) {
        if(toplevel_casted->title == NULL) {
            toplevel_casted->title = malloc(sizeof(char)*80);
            strcpy(toplevel_casted->title, *title);
        }
        else {
            strcpy(toplevel_casted->title, *title);
        }
    }

    if(closable != NULL) {

        if(toplevel_casted->closable && !(*closable)) {
            // Destroy button (children_head is the closing button for sure)
            if(toplevel_casted->widget.children_tail == toplevel_casted->widget.children_head) {
                toplevel_casted->widget.children_tail = NULL;
            }

            ei_widget_t tmp = toplevel_casted->widget.children_head->next_sibling;
            ei_widget_destroy(toplevel_casted->widget.children_head);
            toplevel_casted->widget.children_head = tmp;
        }

        if(!(toplevel_casted->closable) && *closable) {
            // Create new button
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


            // Put button in first place of children
            ei_widget_t first_child = toplevel->children_head;
            if(first_child != close) {
                // At least two children
                if(first_child->next_sibling == close) {
                    // Exactly two children
                    close->next_sibling = first_child;
                    toplevel->children_tail = first_child;
                    first_child->next_sibling = NULL;
                    toplevel->children_head = close;
                }
                else {
                    // More than two children
                    ei_widget_t child = toplevel->children_head;
                    while(child->next_sibling != close) {
                        child = child->next_sibling;
                    }

                    child->next_sibling = NULL;
                    toplevel->children_tail = child;
                    close->next_sibling = first_child;
                    toplevel->children_head = close;
                }
            }
        }

        toplevel_casted->closable = *closable;

    }

    if(resizable != NULL) toplevel_casted->resizable = *resizable;
    if(min_size != NULL) toplevel_casted->min_size = *min_size;
}

