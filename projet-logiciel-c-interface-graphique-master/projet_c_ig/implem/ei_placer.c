#include "ei_placer.h"
#include "classes.h"


void		ei_place	(ei_widget_t		widget,
				 ei_anchor_t*		anchor,
				 int*			x,
				 int*			y,
				 int*			width,
				 int*			height,
				 float*			rel_x,
				 float*			rel_y,
				 float*			rel_width,
				 float*			rel_height)
{
    if(widget->placer_params == NULL) {
        // Create new placer
        ei_impl_placer_params_t* placer_params = malloc(sizeof(struct ei_impl_placer_params_t));

        placer_params->anchor = (anchor != NULL) ? *anchor : ei_anc_northwest;
        placer_params->x = (x != NULL) ? *x : 0;
        placer_params->y = (y != NULL) ? *y : 0;
        placer_params->width = (width != NULL) ? *width : (rel_width != NULL) ? 0 : widget->requested_size.width;
        placer_params->height = (height != NULL) ? *height : (rel_height != NULL) ? 0 : widget->requested_size.height;
        placer_params->rel_x = (rel_x != NULL) ? *rel_x : 0.0;
        placer_params->rel_y = (rel_y != NULL) ? *rel_y : 0.0;
        placer_params->rel_width = (rel_width != NULL) ? *rel_width : 0.0;
        placer_params->rel_height = (rel_height != NULL) ? *rel_height : 0.0;

        widget->placer_params = placer_params;
    }
    else {
        // Update placer
        if(anchor != NULL) widget->placer_params->anchor = *anchor;
        if(x != NULL) widget->placer_params->x = *x;
        if(y != NULL) widget->placer_params->y = *y;
        if(width != NULL) widget->placer_params->width = *width;
        if(height != NULL) widget->placer_params->height = *height;
        if(rel_x != NULL) widget->placer_params->rel_x = *rel_x;
        if(rel_y != NULL) widget->placer_params->rel_y = *rel_y;
        if(rel_width != NULL) widget->placer_params->rel_width = *rel_width;
        if(rel_height != NULL) widget->placer_params->rel_height = *rel_height;
    }
    
    // Update widget's geometry
    widget->wclass->geomnotifyfunc(widget);

    // Update all descendant's geometry
    ei_widget_t child = widget->children_head;
    while(child != NULL) {
        child->wclass->geomnotifyfunc(child);
        child = child->next_sibling;
    }
}


void ei_placer_forget(ei_widget_t widget)
{
    if(widget->placer_params != NULL) {
        free(widget->placer_params);
    }
}