

#ifndef PROJETC_IG_POSITION_H
#define PROJETC_IG_POSITION_H

#include "ei_types.h"
#include "ei_event.h"
#include "classes.h"


/* click_in_square :
 *
 *  Return true if click is inside the resize button, otherwise return false
 *
 */
bool click_in_square(ei_point_t click, ei_point_t top_left, int width, int height, int border_width);

/* click_in_header :
 *
 *  Return true if click is inside the header of the toplevel, otherwise return false
 *
 */
bool click_in_header(ei_point_t click, ei_point_t top_left, int width, int height, int border_width);

/* put_to_last_children :
 *
 *  move toplevel at the end of the linked list of the children of his parent
 *
 */
void put_to_last_children(ei_widget_t toplevel);

/* in_rect :
 *
 *  Return true the point is inside the rect, otherwise return false
 *
 */
bool in_rect(ei_point_t point, ei_rect_t rect);

/*
 *  convert_point :
 *
 *  converts a ei_anchor_t object to the ei_point_t point corresponding in the rect
 *
 */
ei_point_t* convert_point(ei_anchor_t txt_anchor, ei_rect_t rect, ei_point_t* pts_pt);

#endif //PROJETC_IG_POSITION_H
