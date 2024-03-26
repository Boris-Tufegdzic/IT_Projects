#include "aux_ei_draw.h"


void swap_coord(int* x0, int* y0, int* x1, int *y1)
{
	int temp = *x1;
	*x1 = *x0;
	*x0 = temp;
	temp = *y1;
	*y1 = *y0;
	*y0 = temp;
}

void set_color(uint32_t* color_value, const ei_color_t* color, int ir, int ig, int ib, int ia)
{
	uint8_t* color_ptr = (uint8_t*)color_value;

	if(color != NULL){
		color_ptr[ir] = color->red;
		color_ptr[ig] = color->green;
		color_ptr[ib] = color->blue;
		// ia = 6 - ir - ig - ib
		if(ia != -1)
			color_ptr[ia] = color->alpha;
	} else {
		color_ptr[ir] = 0x00;
		color_ptr[ig] = 0x00;
		color_ptr[ib] = 0x00;
		if(ia != -1)
			color_ptr[ia] = 0xFF;
	}
}

bool brute_clipping(ei_rect_t limit_rect, ei_rect_t* dst_rect, ei_rect_t* src_rect)
{
	int x_min = limit_rect.top_left.x;
	int x_max = limit_rect.top_left.x + limit_rect.size.width;
	int y_min = limit_rect.top_left.y;
	int y_max = limit_rect.top_left.y + limit_rect.size.height;

	// if anchor is on the right of the surface or below the surface, then the entire rectangle is outside of the surface
	if (dst_rect->top_left.y + dst_rect->size.height < y_min
	    || y_max < dst_rect->top_left.y
	    || dst_rect->top_left.x + dst_rect->size.width < x_min
	    || x_max < dst_rect->top_left.x)
	{return false;}

	// Adjusting the size of both source and destination rectangles
	// anchor on the left of the surface
	if(dst_rect->top_left.x < x_min){
		dst_rect->size.width -= (x_min - dst_rect->top_left.x);
		src_rect->size.width = dst_rect->size.width;
		src_rect->top_left.x += x_min - dst_rect->top_left.x;
		dst_rect->top_left.x = x_min;
	}

	// anchor over the surface
	if(dst_rect->top_left.y < y_min){
		dst_rect->size.height -= (y_min - dst_rect->top_left.y);
		src_rect->size.height = dst_rect->size.height;
		src_rect->top_left.y += y_min - dst_rect->top_left.y;
		dst_rect->top_left.y = y_min;
	}

	// end on the right of the surface
	if(dst_rect->top_left.x + dst_rect->size.width > x_max){
		dst_rect->size.width = x_max - dst_rect->top_left.x;
		src_rect->size.width = dst_rect->size.width;
	}

	// end below the surface
	if(dst_rect->top_left.y + dst_rect->size.height > y_max){
		dst_rect->size.height = y_max - dst_rect->top_left.y;
		src_rect->size.height = dst_rect->size.height;
	}

	return true;
}

void draw_pixel(int x, int y, ei_color_t color, ei_surface_t surface, const ei_rect_t* clipper)
{
	// brute force clipping (to be improved)
	bool coord_in_clipper = true;
	if (clipper != NULL) {
		coord_in_clipper = (clipper->top_left.x < x
				    && x < clipper->top_left.x + clipper->size.width
				    && clipper->top_left.y < y
				    && y < clipper->top_left.y + clipper->size.height);
	}

	ei_rect_t surf_rect = hw_surface_get_rect(surface);
	bool is_in = x > surf_rect.top_left.x && x < surf_rect.top_left.x + surf_rect.size.width && y > surf_rect.top_left.y && y < surf_rect.top_left.y + surf_rect.size.height;

	if (coord_in_clipper && is_in){
		uint32_t* buffer = (uint32_t*)hw_surface_get_buffer(surface);
		int index = get_index_from_coord((ei_point_t){x, y}, surface);

		int ir, ig, ib, ia;
		hw_surface_get_channel_indices(surface, &ir, &ig, &ib, &ia);

		uint32_t color_value;
		set_color(&color_value, &color, ir, ig, ib, ia);

		buffer[index] = color_value;
	}
}

void draw_line(int x0, int y0, int x1, int y1, ei_color_t color, ei_surface_t surface, const ei_rect_t* clipper)
{
	/* Coordinates exchange for symmetric cases */
	if((y1 <= y0) && !(((y0==y1) && (x0 < x1))))
		swap_coord(&x0, &y0, &x1, &y1);

	if((x0 == x1) && (y0 < y1)){
		while(y0 < y1){
			draw_pixel(x0, y0, color, surface, clipper);
			y0++;
		}
	}
	else if((y0 == y1) && (x0 < x1)){
		while(x0 < x1){
			draw_pixel(x0, y0, color, surface, clipper);
			x0++;
		}
	}
	else if((x0 < x1) && (y0 < y1)){
		int dx = x1 - x0;
		int dy = y1 - y0;
		int e  = 0;
		if( dx > dy){
			while(x0 < x1) {
				draw_pixel(x0, y0, color, surface, clipper);
				x0++;
				e += dy;
				if(2 * e > dx){
					y0++;
					e -= dx;
				}
			}
		} else {
			while(y0 < y1) {
				draw_pixel(x0, y0, color, surface, clipper);
				y0++;
				e += dx;
				if(2 * e > dy) {
					x0++;
					e-= dy;
				}
			}
		}
	}
	else if ((x1 < x0) && (y0 < y1)) {
		int dx = x0 - x1;
		int dy = y1 - y0;
		int e = 0;
		if(dx > dy) {
			while(x0 > x1) {
				draw_pixel(x0, y0, color, surface, clipper);
				x0--;
				e += dy;
				if(2 * e > dx) {
					y0++;
					e -= dx;
				}
			}
		} else {
			while(y0 < y1) {
				draw_pixel(x0, y0, color, surface, clipper);
				y0 ++;
				e += dx;
				if (2 * e > dy) {
					x0 -= 1;
					e -= dy;
				}
			}
		}
	}
	else draw_pixel(x0, y0, color, surface, clipper);
}

int abs_y_min(ei_point_t point1, ei_point_t point2){
	if(point1.y<point2.y){
		return point1.x;
	} else {
		return point2.x;
	}
}

int array_y_min(ei_point_t* point_array, size_t point_array_size){
    if (point_array_size == 0){
        printf("point_array_size ERROR : value 0");
        return EXIT_FAILURE;
    }
	int min = point_array[0].y;
	for(size_t i = 1; i<point_array_size; i++){
		if (point_array[i].y < min) {
			min = point_array[i].y;
		}
	}
	if (min < 0)
		return 0;
	return min;
}

void insert(struct side** dest, struct side** src){
	struct side* curr = *dest;
	if(curr == NULL){
		*dest = *src;
	} 
    else {
		while(curr->next != NULL){
			curr = curr->next;
		}
		curr->next = *src;
	}
}

struct side* create_side(ei_point_t pts1, ei_point_t pts2){
	struct side* new_side = malloc(sizeof(struct side));

	float slope = (float)(pts1.x - pts2.x)/(float)(pts1.y - pts2.y);
	new_side->y_max = max(pts1.y, pts2.y);
	new_side->x_y_min = abs_y_min(pts1, pts2);
	new_side->x_inter = new_side->x_y_min;
	new_side->slope = slope;
	new_side->next = NULL;
	return new_side;
}


struct side** init_sides(ei_point_t* point_array, size_t point_array_size, ei_surface_t	surface)
{

	ei_size_t size = hw_surface_get_size(surface);

	struct side** TC = malloc(sizeof(struct side)*(size.height));
	for (int i = 0; i < size.height; i++)
		TC[i] = NULL;

	for(size_t k = 0; k < point_array_size; k++){
		ei_point_t p1 = point_array[k];
		ei_point_t p2 = (k+1 == point_array_size)? point_array[0] : point_array[k+1];

		if(p1.y != p2.y){

			int index = min(p1.y, p2.y);
			index = min(index, size.height-1);

			bool real_side = p1.y >= 0 || p2.y >= 0;

			if (real_side) {
				struct side *new_side = create_side(p1, p2);
				if (index < 0) {
					new_side->x_inter = new_side->x_inter + (-index) * new_side->slope;
					insert(&TC[0], &new_side);
				} else {
					insert(&TC[index], &new_side);
				}

			}
		}
	}
	return TC;
}

void delete_sides(struct side** TCA, int y) 
{
	struct side* cur_side = *TCA;
	struct side* prev_side = NULL;
	while(cur_side != NULL){
		if(cur_side->y_max == y){
			if(prev_side == NULL){
				*(TCA) = cur_side->next;
				struct side* temp = cur_side;
				free(temp);
				cur_side = *(TCA);
			} else {
				prev_side->next = cur_side->next;
				struct side* temp = cur_side;
				free(temp);
				cur_side = prev_side->next;
			}
		} else {
			prev_side = cur_side;
			cur_side = cur_side->next;
		}
	}
}

int linked_list_size(struct side *a)
{
	struct side* cur_side = a;
	int count = 0;
	while(cur_side != NULL){
		count++;
		cur_side = cur_side->next;
	}
	return count;
}

void sort_sides(struct side** a)
{
	int array_size = linked_list_size(*a);
	if (array_size == 0) return;
	struct side** array = malloc(array_size*sizeof(struct side*));

	/* filling the array */
	struct side* cur_side = *a;
	for (int i = 0; i < array_size; i++){
		array[i] = cur_side;
		cur_side = cur_side->next;
	}

	/* sort with respect to x_inter */
	for (int i = 0; i < array_size; i++){
		int k = i;
		struct side* v = array[i];
		while((k-1>=0) && ((array[k-1])->x_inter > (v->x_inter))){
			array[k] = array[k-1];
			k--;
		}
		array[k] = v;
	}

	struct side* sorted_list = array[0];
	struct side* current_side = sorted_list;
	for (int i = 1; i < array_size; i++){
		current_side->next = array[i];
		current_side = current_side->next;
	}
	current_side->next = NULL;
	*a = sorted_list;

    free(array);
}

void fill_polygon(int y, struct side* TCA, ei_surface_t surface, ei_color_t color, const ei_rect_t* clipper) 
{
	float prev = 0;
	int count = 0;
	struct side *curr = TCA;
	while (curr != NULL) {
		if (count % 2 == 1) {
			prev = ceil(prev);
			int x_max = floor(curr->x_inter);
			for (int x = prev; x < x_max; x++) {
				draw_pixel(x, y, color, surface, clipper);
			}
		}
		prev = curr->x_inter;
		curr = curr->next;
		count += 1;
	}
}

void update_x_inter(struct side** TCA) 
{
	struct side* cur_side = *TCA;
	while (cur_side != NULL){
		cur_side->x_inter+=cur_side->slope;
		cur_side = cur_side->next;
	}
}

ei_point_t get_point(ei_point_t middle, int radius, float angle)
{
    ei_point_t new_point;
    new_point.x = middle.x + (int)(radius*cos(angle));
    new_point.y = middle.y + (int)(radius*sin(angle));

    return new_point;
}

float degree_to_radian(int angle)
{
    float new_angle = (float)(angle*(M_PI/180));

    return new_angle;
}

size_t get_point_array_size(int radius, int starting_angle, int ending_angle)
{
    /** divider can be modified **/
    int divider = radius * (ending_angle - starting_angle)/90;
    size_t size = (size_t)divider;

    return size;
}


ei_point_t* arc(ei_point_t middle, int radius, int starting_angle, int ending_angle)
{
    size_t point_array_size = get_point_array_size(radius, starting_angle, ending_angle);
    ei_point_t* point_array = malloc(sizeof(ei_point_t)*point_array_size);

    float angle1 = degree_to_radian(starting_angle);
    float angle2 = degree_to_radian(ending_angle);

    for (size_t i = 0; i < point_array_size; i++){
        float angle = angle1 + (float)(i*(angle2-angle1)/(point_array_size-1));
        point_array[i] = get_point(middle, radius, angle);
    }

    return point_array;
}



size_t rf_get_size(int radius, int type)
{
    size_t size = get_point_array_size(radius, 0, 90);
    if (type == 0){
        if (radius == 0) return 4;
        return 4*size;
    } 
    else if (type == 1 || type == 2) {
        if (radius == 0) return 5;
        return 2 * (size+2);
    } 
    else {
        printf("Type error must be : 0, 1 or 2");
        return 0;
    }
}

ei_point_t* rf_get_full_array(ei_rect_t rect, int radius)
{
    int width = rect.size.width;
    int height = rect.size.height;

    if (radius == 0){
        ei_point_t* point_array = malloc(sizeof(ei_point_t)*4);
        ei_point_t top_left = {rect.top_left.x , rect.top_left.y };
        ei_point_t top_right = {rect.top_left.x + width , rect.top_left.y };
        ei_point_t bottom_right = {rect.top_left.x + width, rect.top_left.y + height };
        ei_point_t bottom_left = {rect.top_left.x , rect.top_left.y + height };
        point_array[0] = top_left;
        point_array[1] = top_right;
        point_array[2] = bottom_right;
        point_array[3] = bottom_left;

        return point_array;
    }

    ei_point_t top_left = {rect.top_left.x + radius, rect.top_left.y + radius};
    ei_point_t top_right = {rect.top_left.x + width - radius, rect.top_left.y + radius};
    ei_point_t bottom_right = {rect.top_left.x + width - radius, rect.top_left.y + height - radius};
    ei_point_t bottom_left = {rect.top_left.x + radius, rect.top_left.y + height - radius};

    ei_point_t middles_array[4] = {bottom_right, bottom_left, top_left, top_right};

    size_t size = get_point_array_size(radius, 0, 90);

    ei_point_t *point_array = malloc(sizeof(ei_point_t) * size * 4);
    ei_point_t *array;

    for (size_t i = 0; i < 4; i++) {
        array = arc(middles_array[i], radius, i * 90, 90 + 90 * i);
        for (size_t j = 0; j < size; j++) {
            point_array[i * size + j] = array[j];
        }
        ei_point_t* ptr_to_free = array;
        free(ptr_to_free);

    }

    return point_array;
}

ei_point_t* rf_get_bottom_array(ei_rect_t rect, ei_point_t* full_array, int radius) {

    int width = rect.size.width;
    int height = rect.size.height;

    if (radius == 0){
        ei_point_t* bottom_array = malloc(5*sizeof(ei_point_t));
        ei_point_t p1 = {rect.top_left.x + height / 2, rect.top_left.y + height / 2};
        ei_point_t p2 = {rect.top_left.x + width - height / 2, rect.top_left.y + height / 2};
        bottom_array[0] = p1;
        bottom_array[1] = p2;
        bottom_array[2] = full_array[1];
        bottom_array[3] = full_array[0];
        bottom_array[4] = full_array[3];

        return bottom_array;
    }

    size_t size = get_point_array_size(radius, 0, 90);

    ei_point_t* bottom_array =  malloc((2*(size+2)*sizeof(ei_point_t)));

    size_t start = 3 * size + size/2 - 1;
    size_t end =  size + size /2 + 1;

    size_t j = 0;

    ei_point_t p1 = {rect.top_left.x + height / 2, rect.top_left.y + height / 2};
    ei_point_t p2 = {rect.top_left.x + width - height / 2, rect.top_left.y + height / 2};
    bottom_array[j] = p1;
    j++;
    bottom_array[j] = p2;
    j++;

    for (size_t i = start; i < 4 * size; i++) {
        bottom_array[j] = full_array[i];
        j++;
    }
    for (size_t i = 0; i < end && j < 2*(size+4); i++) {
        bottom_array[j] = full_array[i];
        j++;
    }


    return bottom_array;

}

ei_point_t* rf_get_top_array(ei_rect_t rect, ei_point_t* full_array, int radius) {

    int width = rect.size.width;
    int height = rect.size.height;
    if (radius == 0) {
        ei_point_t *top_array = malloc(5 * sizeof(ei_point_t));
        ei_point_t p1 = {rect.top_left.x + height / 2, rect.top_left.y + height / 2};
        ei_point_t p2 = {rect.top_left.x + width - height / 2, rect.top_left.y + height / 2};
        top_array[0] = p1;
        top_array[1] = p2;
        top_array[2] = full_array[1];
        top_array[3] = full_array[2];
        top_array[4] = full_array[3];

        return top_array;

    }
    size_t size = get_point_array_size(radius, 0, 90);

    ei_point_t* top_array = malloc((2*(size+2)*sizeof(ei_point_t)));

    size_t start =  size + size/2 - 1;
    size_t end =  3 * size + size/2 + 1;

    size_t j = 0;

    ei_point_t p1 = {rect.top_left.x + height / 2, rect.top_left.y + height / 2};
    ei_point_t p2 = {rect.top_left.x + width - height / 2, rect.top_left.y + height / 2};
    top_array[j] = p2;
    j++;
    top_array[j] = p1;
    j++;

    for (size_t i = start; i < end && j < 2*(size+2); i++) {
        top_array[j] = full_array[i];
        j++;
    }


    return top_array;
}


ei_point_t* rounded_frame(ei_rect_t rect, int radius, int type)
{
    ei_point_t* full_array = rf_get_full_array(rect, radius);

    if (type == 0) {
        return full_array;
    } else if (type == 1) {
        ei_point_t *bottom_array = rf_get_bottom_array(rect, full_array, radius);
        free(full_array);
        return bottom_array;
    } else if (type == 2) {
        ei_point_t *top_array = rf_get_top_array(rect, full_array, radius);
        free(full_array);
        return top_array;
    } else {
        printf("type value ERROR : must be 0, 1 or 2");
        return NULL;
    }
}

void draw_button(ei_surface_t surface, ei_rect_t rect, int radius, int thickness, ei_color_t color, ei_rect_t* clipper, ei_relief_t relief)
{


    if (relief == ei_relief_none){
        thickness = 0;
    }

	int width = rect.size.width;
	int height = rect.size.height;

	if (radius > width / 2) radius = width/2;
	if (radius > height / 2) radius = height/2;
	if (thickness > width / 2) thickness = width/2;
	if (thickness > height / 2) thickness = height/2;


	int inner_width = width - 2*thickness;
	int inner_height = height - 2*thickness;

	ei_size_t size = {inner_width, inner_height};
	ei_point_t top_left = {rect.top_left.x + thickness, rect.top_left.y + thickness};

	ei_rect_t inner_rect = {top_left, size};

    ei_color_t light_color = {(int)(color.red*1.25), (int)(color.green*1.25), (int)(color.blue*1.25), color.alpha};
    ei_color_t dark_color = {(int)(color.red*0.75), (int)(color.green*0.75), (int)(color.blue*0.75), color.alpha};
    ei_color_t temp;

    switch(relief)
    {
        case ei_relief_none:
            light_color = color;
            dark_color = color;
            break;

        case ei_relief_raised:
            break;

        case ei_relief_sunken:
            temp = dark_color;
            dark_color = light_color;
            light_color = temp;
            break;
    }

	ei_point_t* light_button = rounded_frame(rect, radius, 2);
	size_t light_button_size = rf_get_size(radius, 2);
	ei_point_t* dark_button = rounded_frame(rect, radius, 1);
	size_t dark_button_size = rf_get_size(radius, 1);
	ei_point_t* button = rounded_frame(inner_rect, radius, 0);
	size_t button_size = rf_get_size(radius, 0);

	ei_draw_polygon(surface, light_button, light_button_size, light_color, clipper);
	ei_draw_polygon(surface, dark_button, dark_button_size, dark_color, clipper);
	ei_draw_polygon(surface, button, button_size, color, clipper);
    free(light_button);
    free(dark_button);
    free(button);
}


void draw_rectangle(ei_surface_t surface, ei_rect_t rect, ei_color_t color, ei_rect_t* clipper)
{
	ei_point_t corners[4];
	
	corners[0] = rect.top_left;
	corners[1] = (ei_point_t) {rect.top_left.x + rect.size.width, rect.top_left.y};
	corners[2] = (ei_point_t) {rect.top_left.x + rect.size.width, rect.top_left.y + rect.size.height};
	corners[3] = (ei_point_t) {rect.top_left.x, rect.top_left.y + rect.size.height};
	
	ei_draw_polygon(surface, corners, 4, color, clipper);
}

//Cohen-Sutherland Codes
#define INSIDE 0        // 0000
#define LEFT 1          // 0001
#define RIGHT 2         // 0010
#define BOTTOM 8        // 1000
#define TOP 4           // 0100
#define TOP_LEFT 5      // 0101
#define TOP_RIGHT 6     // 0110
#define BOTTOM_LEFT 9   // 1001
#define BOTTOM_RIGHT 10 // 1010

int get_code(ei_point_t point, int xmin, int xmax, int ymin, int ymax)
{
    int x = point.x;
    int y = point.y;
    if(x<xmin){
        //TOP LEFT
        if(y<ymin){
            return 5;
        }
        //BOTTOM LEFT
        else if(y>ymax){
            return 9;
        }
        //LEFT
        else{
            return 1;
        }
    }
    else if(x>xmax){
        //TOP RIGHT
        if(y<ymin){
            return 6;
        }
        //BOTTOM RIGHT
        else if(y>ymax){
            return 10;
        }
        //RIGHT
        else{
            return 2;
        }
    }
    else{
        //TOP
        if(y<ymin){
            return 4;
        }
        //BOTTOM
        else if(y>ymax){
            return 8;
        }
        //INSIDE
        else{
            return 0;
        }
    }
}


bool is_btw(int x, int xmin, int xmax)
{
    return(x>=xmin && x<=xmax);
}


ei_segment_t* get_intersection_line(ei_segment_t* seg_ptr, const ei_rect_t* clipper)
{
    ei_segment_t seg = *seg_ptr;
    ei_point_t p1 = seg.p1;
    ei_point_t p2 = seg.p2;
    int xmin = clipper->top_left.x;
    int ymin = clipper->top_left.y;
    int xmax = clipper->top_left.x + clipper->size.width;
    int ymax = clipper->top_left.y + clipper->size.height;
    int code1 = get_code(p1, xmin, xmax, ymin, ymax);
    int code2 = get_code(p2, xmin, xmax, ymin, ymax);
    //Trivial cases
    //Completely inside
    if((code1 | code2) == 0){
        return seg_ptr;
    }
    //Completely outside
    else if((code1 & code2) != 0){
        return NULL;
    }
    //Other non trivial cases
    //p1 becomes the point with the lowest y coordinate
	ei_point_t tamp1 = p1;
	ei_point_t tamp2 = p2;
    p1 = (p1.y > p2.y) ? p1 : p2;
    p2 = (p1.y > p2.y) ? p2 : tamp1;
    code1 = (tamp1.y > tamp2.y) ? code1 : code2;
    code2 = (tamp1.y > tamp2.y) ? code2 : code1;
    int x = p1.x;
    int y = p1.y;
    if(p1.x != p2.x && p1.y != p2.y){
        double slope_x = (double)(p2.y - p1.y)/(double)(p2.x - p1.x);
        double slope_y = (double)(p2.x - p1.x)/(double)(p2.y - p1.y); 
        switch(code1){
            case BOTTOM:
                if (is_btw((int)(x + (ymax - y)*slope_y), xmin, xmax)){
                    p1 = (ei_point_t){(int)(x + (ymax - y)*slope_y), ymax};
                    if(code2 != 0){
                        if (is_btw((int)(y + (xmin - x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)};
                        }
                        else if (is_btw((int)(y + (xmax - x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)};
                        }
                        else if (is_btw((int)(x + (ymin - y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin};
                        }
                        else{
                            printf("ERROR in case BOTTOM\n");
                        }
                    }
                }
                else{
                    return NULL;
                }
                break;
            case BOTTOM_LEFT:
                if (is_btw((int)(y + (xmin - x)*slope_x), ymin, ymax) || is_btw((int)(x + (ymax - y)*slope_y), xmin, xmax)){
                    p1 = (is_btw((int)(y + (xmin - x)*slope_x), ymin, ymax)) ? (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)} : (ei_point_t){(int)(x + (ymax - y)*slope_y), ymax};
                    if(code2 != 0){
                        if (is_btw((int)(x + (ymin - y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin};
                        }
                        else if (is_btw((int)(y + (xmax - x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)};
                        }
                        else{
                            printf("ERROR in case BOTTOM_LEFT");
                        }
                    }
                }
                else{
                    return NULL;
                }
                break;
            case BOTTOM_RIGHT:
                if (is_btw((int)(y + (xmax - x)*slope_x), ymin, ymax) || is_btw((int)(x + (ymax - y)*slope_y), xmin, xmax)){
                p1 = (is_btw((int)(y + (xmax - x)*slope_x), ymin, ymax)) ? (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)} : (ei_point_t){(int)(x + (ymax - y)*slope_y), ymax};
                if(code2 != 0){
                        if (is_btw((int)(y + (xmin - x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)};
                        }
                        else if (is_btw((int)(x + (ymin - y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin};
                        }
                        else{
                            printf("ERROR in case BOTTOM_RIGHT");
                        }
                }
                }
                else{
                    return NULL;
                }
                break;
            case RIGHT:
                if (is_btw((int)(y + (xmax-x)*slope_x), ymin, ymax)){
                    p1 = (ei_point_t){xmax, (int)(y + (xmax-x)*slope_x)};
                    if(code2 != 0){
                        if (is_btw((int)(x + (ymin-y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymin-y)*slope_y), ymin};
                        }
                        else if (is_btw((int)(x + (ymax-y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymax-y)*slope_y), ymax};
                        }
                        else if (is_btw((int)(y + (xmin-x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmin, (int)(y + (xmin-x)*slope_x)};
                        }
                        else{
                            printf("ERROR in case RIGHT");
                        }
                    }
                }
                else{
                    return NULL;
                }
                break;
            case LEFT:
                if (is_btw((int)(y + (xmin - x)*slope_x), ymin, ymax)){
                    p1 = (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)};
                    if(code2 != 0){
                        if (is_btw((int)(x + (ymin-y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymin-y)*slope_y), ymin};
                        }
                        else if (is_btw((int)(x + (ymax-y)*slope_y), xmin, xmax)){
                            p2 = (ei_point_t){(int)(x + (ymax-y)*slope_y), ymax};
                        }
                        else if (is_btw((int)(y + (xmax - x)*slope_x), ymin, ymax)){
                            p2 = (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)};
                        }
                        else{
                            printf("ERROR in case LEFT");
                        }
                    }
                }
                else{
                    return NULL;
                }
                break;
            case INSIDE:
                switch(code2){
                    case LEFT:
                        p2 = (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)};
                        break;
                    case RIGHT:
                        p2 = (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)};
                        break;
                    case TOP:
                        p2 = (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin};
                        break;
                    case TOP_RIGHT:
                        p2 = (is_btw((int)(x + (ymin - y)*slope_y), xmin, xmax)) ? (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin} : (ei_point_t){xmax, (int)(y + (xmax - x)*slope_x)};
                        break;
                    case TOP_LEFT:
                        p2 = (is_btw((int)(x + (ymin - y)*slope_y), xmin, xmax)) ? (ei_point_t){(int)(x + (ymin - y)*slope_y), ymin} : (ei_point_t){xmin, (int)(y + (xmin - x)*slope_x)};
                        break;
                }
        }
    }
    else if(p1.x == p2.x){
        if(code1 == INSIDE){
            p2 = (ei_point_t){x, ymin};
        }
        else{
            p1 = (ei_point_t){x, ymax};
        }
    }
    else if(p1.y == p2.y){
        if(code1 == INSIDE){
            if(code2 == LEFT){
                p2 = (ei_point_t){xmin, y};
            }
            else{
                p2 = (ei_point_t){xmax, y};
            }
        }
        else if(code1 == LEFT){
            if(code2 == INSIDE) {
                p1 = (ei_point_t) {xmin, y};
            }
            else{
                p1 = (ei_point_t){xmin, y};
                p2 = (ei_point_t){xmax, y};
            }
        }
        else if(code1 == RIGHT){
            if(code2 == INSIDE){
                p1 = (ei_point_t){xmax, y};
            }
            else{
                p1 = (ei_point_t){xmax, y};
                p2 = (ei_point_t){xmin, y};
            }
        }
    }
    *seg_ptr = (ei_segment_t){p1, p2, NULL};
    return seg_ptr; 
}


void insert_seg(ei_segment_t** linked_list_ptr, ei_segment_t* seg_ptr)
{
    ei_segment_t* linked_list = *linked_list_ptr;
    if(linked_list == NULL){
        *linked_list_ptr = seg_ptr;
    }
    else{
        ei_segment_t* curr = *linked_list_ptr;
        while(curr->next != NULL){
            curr = curr->next;
        }
        curr->next = seg_ptr;
    }
}
