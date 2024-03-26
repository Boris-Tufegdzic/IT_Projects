#ifndef AUX_H
#define AUX_H

#include "ei_draw.h"

/*  min :
 *
 *  return the minimum value of two int
 *
 *  */
int min(int int1, int int2);

/*  max :
 *
 *  return the maximum value of two int
 *
 *  */
int max(int int1, int int2);

/*  get_index_from_coord :
 *
 *  return the index in the array buffer of a point
 *
 *  */
int get_index_from_coord(ei_point_t point, ei_surface_t surface);
/*  get_color :
 *
 *  return the color pixel of the pick_surface which location is the same as point
 *
 *  */
ei_color_t get_color(ei_surface_t pick_surface, ei_point_t point);
/*  same_color :
 *
 *  return true if color1 and color2 are the same colors
 *
 *  */
bool same_color(ei_color_t color1, ei_color_t color2);
#endif