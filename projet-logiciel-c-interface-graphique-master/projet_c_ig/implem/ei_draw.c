#include <stdlib.h>
#include <stdbool.h>
#include "ei_draw.h"
#include "aux_ei_draw.h"


void ei_draw_polyline(ei_surface_t surface, ei_point_t* point_array, size_t	point_array_size, ei_color_t color, const ei_rect_t* clipper){
	
	ei_segment_t* linked_seg_list = NULL;
    for (size_t i = 0 ; i < point_array_size-1 ; i++) 
	{
		ei_point_t p1 = point_array[i];
		ei_point_t p2 = point_array[i+1];
        ei_segment_t seg = (ei_segment_t){p1, p2, NULL};
        ei_segment_t* inter_seg_ptr = get_intersection_line(&seg, clipper);

        if(inter_seg_ptr != NULL) {
            insert_seg(&linked_seg_list, inter_seg_ptr);
        }
	}

    ei_segment_t* curr = linked_seg_list;

    while(curr != NULL) {
        draw_line(curr->p1.x, curr->p1.y, curr->p2.x, curr->p2.y, color, surface, clipper);
        curr = curr->next;
    }
}


void ei_draw_polygon (ei_surface_t surface, ei_point_t* point_array, size_t point_array_size, ei_color_t color, const ei_rect_t* clipper) {

	int height = hw_surface_get_size(surface).height;

	int y =  array_y_min(point_array, point_array_size);
	struct side **TC = init_sides(point_array, point_array_size, surface);
	struct side *TCA = NULL;

	do {
		if (y < height) {
			insert(&TCA, &TC[y]);
			TC[y] = NULL;
			delete_sides(&TCA, y);
			sort_sides(&TCA);
			fill_polygon(y, TCA, surface, color, clipper);
			y++;
			update_x_inter(&TCA);
		}
	} while(TCA!=NULL && y < height);

    free(TC);
}


void ei_draw_text(ei_surface_t surface, const ei_point_t* where, ei_const_string_t text, ei_font_t font, ei_color_t	color, const ei_rect_t* clipper)
{
	// To avoid empty text
	if (text[0] != 0) {

		// Computing the size of the text with the given font
		int width, height;
		hw_text_compute_size(text, font, &width, &height);

		// Creating the text surface the given font and color (+ locking)
		ei_surface_t text_surface = hw_text_create_surface(text, font, color);
		hw_surface_lock(text_surface);

		ei_rect_t surf_rect = hw_surface_get_rect(surface);
		ei_rect_t dst_rect = {*where,(ei_size_t){width, height}};
		ei_rect_t src_rect = hw_surface_get_rect(text_surface);

		// implicit clipping (surface)
		bool in_impl = brute_clipping(surf_rect, &dst_rect, &src_rect);

		if(in_impl){
			// explicit clipping (clipper)
			bool in_expl = true;

			if(clipper != NULL)
				in_expl = brute_clipping(*clipper, &dst_rect, &src_rect);

			if(in_expl)
				ei_copy_surface(surface, &dst_rect, text_surface, &src_rect, true);
		}

		hw_surface_unlock(text_surface);
		hw_surface_free(text_surface);
	}

}


void ei_fill(ei_surface_t surface, const ei_color_t* color, const ei_rect_t* clipper)
{	
	// Computing the amount of pixel to set
	ei_size_t size = hw_surface_get_size(surface);
	int pixel_amount = size.width*size.height;

	uint32_t* buffer = (uint32_t*)hw_surface_get_buffer(surface);

	int ir, ig, ib, ia;
	hw_surface_get_channel_indices(surface, &ir, &ig, &ib, &ia);

	uint32_t color_value;
	set_color(&color_value, color, ir, ig, ib, ia);

	if(clipper == NULL) {
		for(int i = 0; i < pixel_amount; i++)
			buffer[i] = color_value;
	}
	else {
		for(int j = 0; j < size.height; j++) {
			for(int i = 0; i < size.width; i++) {

				// brute clipping
				if(clipper->top_left.x < i
				&& i < clipper->top_left.x + clipper->size.width
				&& clipper->top_left.y < j
				&& j < clipper->top_left.y + clipper->size.height)
				{
					int index = i + j*size.width;
					buffer[index] = color_value;
				}
			}
		}
	}
}


int	ei_copy_surface(ei_surface_t destination, const ei_rect_t* dst_rect, ei_surface_t source, const ei_rect_t* src_rect, bool alpha)
{
	ei_point_t dst_origin, src_origin;
	int dst_width, dst_height;
	int src_width, src_height;

	ei_size_t surf_dst_size = hw_surface_get_size(destination);
	ei_size_t surf_src_size = hw_surface_get_size(source);


	if(dst_rect == NULL) {
		dst_origin = (ei_point_t){0,0};

		dst_width = surf_dst_size.width;
		dst_height = surf_dst_size.height;
	}
	else {
		dst_origin = dst_rect->top_left;
		dst_width = dst_rect->size.width;
		dst_height = dst_rect->size.height;
	}

	if(src_rect == NULL) {
		src_origin = (ei_point_t){0,0};

		src_width = surf_src_size.width;
		src_height = surf_src_size.height;
	}
	else {
		src_origin = src_rect->top_left;
		src_width = src_rect->size.width;
		src_height = src_rect->size.height;
	}


	if(dst_height != src_height || dst_width != src_width)
		return 1; // different sizes between source and destination

	// Getting destination and source buffers
	uint32_t* dst_buffer = (uint32_t*)(hw_surface_get_buffer(destination));
	uint32_t* src_buffer = (uint32_t*)(hw_surface_get_buffer(source));

	// Computing indices for color components
	int ir, ig, ib, ia;
	hw_surface_get_channel_indices(source, &ir, &ig, &ib, &ia);

	// Iterating on the rectangle (same size for both surfaces)
	for(uint32_t j = 0; (int)j < dst_height; j++)
		for(uint32_t i = 0; (int)i < dst_width; i++) {

			// Computing indices
			uint32_t dst_index = dst_origin.x + i + (j+dst_origin.y)*surf_dst_size.width;
			uint32_t src_index = src_origin.x + i + (j+src_origin.y)*surf_src_size.width;
			
			// Checking for alpha handling
			if(!alpha) {
				dst_buffer[dst_index] = src_buffer[src_index];
			}
			else {
				uint8_t* dst_color = (uint8_t*)&dst_buffer[dst_index];
				uint8_t* src_color = (uint8_t*)&src_buffer[src_index];

				uint8_t red = (dst_color[ir]*(255-src_color[ia])+src_color[ir]*src_color[ia])/255;
				uint8_t green = (dst_color[ig]*(255-src_color[ia])+src_color[ig]*src_color[ia])/255;
				uint8_t blue = (dst_color[ib]*(255-src_color[ia])+src_color[ib]*src_color[ia])/255;
				uint8_t alpha_c = 0xFF;
			
				uint32_t final_color;
				uint8_t* color_ptr = (uint8_t*)&final_color;
				color_ptr[ir] = red;
				color_ptr[ig] = green;
				color_ptr[ib] = blue;
				color_ptr[ia] = alpha_c;

				dst_buffer[dst_index] = final_color;
			}
		}

	return 0;
}
