#include "aux.h"


int min(int int1, int int2){
	if (int1 < int2){
		return int1;
	} else {
		return int2;
	}
}

int max(int int1, int int2){
	if (int1 > int2){
		return int1;
	} else {
		return int2;
	}
}

int get_index_from_coord(ei_point_t point, ei_surface_t surface)
{
	ei_size_t size = hw_surface_get_size(surface);
	return size.width*point.y + point.x;
}

ei_color_t get_color(ei_surface_t pick_surface, ei_point_t point) {
    ei_size_t pick_size = hw_surface_get_size(pick_surface);
    int width = pick_size.width;
    uint8_t *buffer = hw_surface_get_buffer(pick_surface);
    int ir, ig, ib, ia;
    hw_surface_get_channel_indices(pick_surface, &ir, &ig, &ib, &ia);
    uint8_t red, green, blue, alpha;

    int index = 4 * width * (point.y) + 4 * point.x;

    red = buffer[index + ir];
    blue = buffer[index + ib];
    green = buffer[index + ig];
    if(ia != -1)
        alpha = buffer[index + ia];

    ei_color_t color = (ei_color_t) {red, green, blue, alpha};
    return color;
}

bool same_color(ei_color_t color1, ei_color_t color2){
    return (color1.red   == color2.red
        &&  color1.green == color2.green
        &&  color1.blue  == color2.blue
        &&  color1.alpha == color2.alpha);
}