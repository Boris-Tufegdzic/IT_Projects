#ifndef AUX_EI_DRAW_H
#define AUX_EI_DRAW_H

#include <stdlib.h>
#include <math.h>
#include <stdbool.h>
#include "ei_draw.h"
#include "aux.h"

#ifndef M_PI
    #define M_PI 3.14159265358979323846
#endif



/*  switch_coord :
 *
 *  swap x and y
 *
 *  */
void swap_coord(int* x0, int* y0, int* x1, int *y1);

/*  set_color :
 *
 *  set color to a specific value using an uint32 color value
 *
 *  */
void set_color(uint32_t* color_value, const ei_color_t* color, int ir, int ig, int ib, int ia);

/*  brute_clipping :
 *
 *
 *
 *  */
bool brute_clipping(ei_rect_t limit_rect, ei_rect_t* dst_rect, ei_rect_t* src_rect);

/*******   Drawing functions   ********/

/*  draw_pixel :
 *
 *  fill a pixel with a color at the coordinate {x,y}
 *
 *  */
void draw_pixel(int x, int y, ei_color_t color, ei_surface_t surface, const ei_rect_t* clipper);

/*  draw_line :
 *
 *  draw a line between two points using bresenham algorithm
 *
 *  */
void draw_line(int x0, int y0, int x1, int y1, ei_color_t color, ei_surface_t surface, const ei_rect_t* clipper);

/*******   Draw_polygon   ********/

/*  Struct side :
 *
 *  it's a node of a linked list who represent a side of the polygon
 *
 *  */
struct side {
    int y_max;
    int x_y_min;
    float slope;
    struct side* next;
    float x_inter;
};

/*  abs_y_min :
 *
 *  return the x coordinate of the point with the smallest y coordinate
 *
 *  */
int abs_y_min(ei_point_t point1, ei_point_t point2);


/*  array_y_min :
 *
 *  return the minimum y coordinate of an array of points
 *
 *  */
int array_y_min(ei_point_t* point_array, size_t point_array_size);


/*  insert :
 *
 *  move source at the end of destination
 *
 *  */
void insert(struct side** dest, struct side** src);

/*  create_side :
 *
 *  It creates a knew structure side with two points
 *
 *  */
struct side* create_side(ei_point_t pts1, ei_point_t pts2);



/*  init_sides :
 *
 *  Return the value of initialised TC and put the value x_y_min to x_inter
 *
 *  */
struct side** init_sides(ei_point_t* point_array, size_t point_array_size, ei_surface_t	surface);


/*  delete_sides :
 *
 *  Delete the sides of TCA with y_max = y : need to free outside this function
 *
 *  */
void delete_sides(struct side** TCA, int y);

/*  linked_list_size :
 *
 *  Return the size of the linked list
 *
 *  */
int linked_list_size(struct side *a);

/*  sort_sides :
 *
 *  Sort the linked list with respect to x_inter
 *
 *  it puts all the nodes of the linked list inside an array
 *  and process the insertion sort to this array
 *  then it build back the linked list
 *
 *  */
void sort_sides(struct side** a);

/*  fill_polygon :
 *
 *  fill the inside of the polygon with a color
 *
 *  */
void fill_polygon(int y, struct side* TCA, ei_surface_t surface, ei_color_t color, const ei_rect_t* clipper);

/*  update_x_inter :
 *
 *  modifies the value of x_inter
 *  x_inter = x_inter + slope
 *
 *  */
void update_x_inter(struct side** TCA);

/*******   Arc function   ********/

/*  Get_point :
 *
 *  return {middle.x + r cos(angle), middle.y + r sin(angle)}
 *
 *  */
ei_point_t get_point(ei_point_t middle, int radius, float angle);

/*
 *  Degree_to_radian :
 *
 *  do the conversion from degree to radiant also int to float
 *
 *  */
float degree_to_radian(int angle_in_degree);

/*
 *  get_point_array_size :
 *
 *  return the size of the array return by arc
 *
 */
size_t get_point_array_size(int radius, int start_angle, int end_angle);

/*
 *  Arc :
 *
 *  return an array representing an arc
 *
 *  int start_angle, int end_angle : angles in degree
 *
 */
ei_point_t* arc(ei_point_t middle, int radius, int start_angle, int end_angle);

/*
 *  free_point_array :
 *
 *  free memory allocated for point_array
 *
 */
void free_point_array(ei_point_t* point_array, size_t point_array_size);

/*******   rounded frame : rf   ********/

/*
 *  rf_get_size :
 *
 *  return the size of point_array for rounded_frame
 *
 */
size_t rf_get_size(int radius, int type);

/*
 *  rf_get_full_array :
 *
 *  return an array of point representing a rectangular button with rounded angles
 *
 */
ei_point_t* rf_get_full_array(ei_rect_t rect, int radius);

/*
 *  rf_get_bottom_array :
 *
 *  return an array representing the bottom part of the button with rounded angles
 *
 */
ei_point_t* rf_get_bottom_array(ei_rect_t rect, ei_point_t* full_array, int radius);

/*
 *  rf_get_top_array :
 *
 *  return an array representing the top part of the button with rounded angles
 *
 */
ei_point_t* rf_get_top_array(ei_rect_t rect, ei_point_t* full_array, int radius);

/*
 *  rounded_frame :
 *
 *  return an array of point representing a button with rounded angles
 *
 *  int type : 0 : full_array, 1 : bottom_array, 2 : top_array
 *
 */
ei_point_t* rounded_frame(ei_rect_t rect, int radius, int type);

/*
 *  draw_button :
 *
 *  draw a button with relief
 *
 */
void draw_button(ei_surface_t surface, ei_rect_t rect, int radius, int thickness, ei_color_t color, ei_rect_t* clipper, ei_relief_t relief);

void draw_rectangle(ei_surface_t surface, ei_rect_t rect, ei_color_t color, ei_rect_t* clipper);

/**********************ANALYTIC CLIPPING LINES*****************************/

typedef struct ei_segment_t{
    ei_point_t p1;
    ei_point_t p2;
    struct ei_segment_t* next;
} ei_segment_t;
/*
 *  insert_seg :
 *
 *  inserts seg_ptr at the end of the linked list
 *
 */
void insert_seg(ei_segment_t** linked_list_ptr, ei_segment_t* seg_ptr);
/*
 *  get_code :
 *
 *  returns the Cohen-Sutherland Codes of the point parameter
 *
 */
int get_code(ei_point_t point, int xmin, int xmax, int ymin, int ymax);
/*
 *  get_intersection_line :
 *
 *  returns the part of the segment which is contained in the clipper
 *
 */
ei_segment_t* get_intersection_line(ei_segment_t* seg_ptr, const ei_rect_t* clipper);

#endif