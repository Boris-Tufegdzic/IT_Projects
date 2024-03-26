#ifndef CLASSES_H
#define CLASSES_H

#include <stdint.h>
#include "ei_widget.h"
#include "ei_widgetclass.h"


typedef struct ei_impl_placer_params_t
{
    ei_anchor_t anchor;
    int x;
    int y;
    int width;
    int height;
    float rel_x;
    float rel_y;
    float rel_width;
    float rel_height;
} ei_impl_placer_params_t;


// Common widget
typedef struct ei_impl_widget_t
{
    ei_widgetclass_t*	wclass;		///< The class of widget of this widget. Avoids the field name "class" which is a keyword in C++.
	uint32_t		pick_id;	///< Id of this widget in the picking offscreen.
	ei_color_t		pick_color;	///< pick_id encoded as a color.
	void*			user_data;	///< Pointer provided by the programmer for private use. May be NULL.
	ei_widget_destructor_t	destructor;	///< Pointer to the programmer's function to call before destroying this widget. May be NULL.
    
    /* Widget Hierarchy Management */
	ei_widget_t		parent;		///< Pointer to the parent of this widget.
	ei_widget_t		children_head;	///< Pointer to the first child of this widget.	Children are chained with the "next_sibling" field.
	ei_widget_t		children_tail;	///< Pointer to the last child of this widget.
	ei_widget_t		next_sibling;	///< Pointer to the next child of this widget's parent widget.

	/* Geometry Management */
	ei_impl_placer_params_t* placer_params;	///< Pointer to the placer parameters for this widget. If NULL, the widget is not currently managed and thus, is not displayed on the screen.
	ei_size_t		requested_size;	///< Size requested by the widget (big enough for its label, for example), or by the programmer. This can be different than its screen size defined by the placer.
	ei_rect_t		screen_location;///< Position and size of the widget expressed in the root window reference.
	ei_rect_t		content_rect;	///< Where to place children, when this widget is used as a container. By defaults, points to the screen_location.
} ei_impl_widget_t;


// Toplevel
typedef struct ei_impl_toplevel_t {

    ei_impl_widget_t widget;
    ei_color_t color;
    int border_width;
    ei_string_t title;
    bool closable;
    ei_axis_set_t resizable;
    ei_size_ptr_t min_size;
} ei_impl_toplevel_t;
typedef ei_impl_toplevel_t* ei_toplevel_t;


// Frame
typedef struct ei_impl_frame_t
{
    ei_impl_widget_t widget;
    ei_color_t color;
    int border_width;
    ei_relief_t relief;

    //draw text
    ei_string_t text;
    ei_font_t text_font; 
    ei_color_t text_color;
    ei_anchor_t text_anchor;

    //draw image
    ei_surface_t img;
    ei_rect_ptr_t img_rect;
    ei_anchor_t img_anchor;
} ei_impl_frame_t;
typedef ei_impl_frame_t* ei_frame_t;


// Button
typedef struct ei_impl_button_t
{
    ei_impl_widget_t widget;

    //Same as frame
    ei_color_t color;
    int border_width;
    int corner_radius;
    ei_relief_t relief;

    //draw text
    ei_string_t text;
    ei_font_t text_font; 
    ei_color_t text_color;
    ei_anchor_t text_anchor;

    //draw image
    ei_surface_t img;
    ei_rect_ptr_t img_rect;
    ei_anchor_t img_anchor;

    //Specific to button
    ei_callback_t	callback;
    ei_user_param_t user_param;
    
} ei_impl_button_t;
typedef ei_impl_button_t* ei_button_t;


/** Allocation functions **/

ei_widget_t ei_toplevel_allocfunc();

ei_widget_t ei_frame_allocfunc();

ei_widget_t ei_button_allocfunc();


/** Release functions **/

void ei_toplevel_releasefunc(ei_widget_t toplevel);

void ei_frame_releasefunc(ei_widget_t frame);

void ei_button_releasefunc(ei_widget_t button);


// Draw functions
void ei_toplevel_drawfunc		(ei_widget_t		toplevel,
							 ei_surface_t		surface,
							 ei_surface_t		pick_surface,
							 ei_rect_t*		clipper);

void ei_frame_drawfunc(ei_widget_t frame,
                ei_surface_t surface,
                ei_surface_t pick_surface,
                ei_rect_t* clipper);

void ei_button_drawfunc		(ei_widget_t		button,
							 ei_surface_t		surface,
							 ei_surface_t		pick_surface,
							 ei_rect_t*		clipper);


// Setdefults functions
void	ei_toplevel_setdefaultsfunc	(ei_widget_t		toplevel);

void ei_frame_setdefaultsfunc(ei_widget_t frame);

void	ei_button_setdefaultsfunc	(ei_widget_t		button);


// Geomnotify functions
void	ei_toplevel_geomnotifyfunc	(ei_widget_t		toplevel);

void ei_frame_geomnotifyfunc(ei_widget_t frame);

void	ei_button_geomnotifyfunc	(ei_widget_t		button);


// Handle functions
bool	ei_toplevel_handlefunc		(ei_widget_t		toplevel,
						 	 struct ei_event_t*	event);

bool ei_frame_handlefunc(ei_widget_t frame, struct ei_event_t* event);

bool	ei_button_handlefunc		(ei_widget_t		button,
						 	 struct ei_event_t*	event);


void button_close(ei_widget_t widget, struct ei_event_t* event, ei_user_param_t	user_param);

#endif