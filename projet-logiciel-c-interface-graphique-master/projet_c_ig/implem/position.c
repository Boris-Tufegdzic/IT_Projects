#include "position.h"


bool click_in_square(ei_point_t click, ei_point_t top_left, int width, int height, int border_width){
    int x = top_left.x;
    int y = top_left.y;

    int right = x + width;
    int left = x + width - 3*border_width;
    int bottom = y + height;
    int top = y + height - 3*border_width;

    if ((click.x >= left) && (click.x <=right) && (click.y <= bottom) &&(click.y >= top))
        return true;
    return false;
}


bool click_in_header(ei_point_t click, ei_point_t top_left, int width, int height, int border_width){
    int top = top_left.y;
    int bottom = top_left.y + height + 2*border_width;
    int left = top_left.x;
    int right = top_left.x + width;
    if ((click.x >= left) && (click.x <=right) && (click.y <= bottom) &&(click.y >= top))
        return true;
    return false;
}

void put_to_last_children(ei_widget_t toplevel){

    if (toplevel->parent->children_tail == toplevel) return;

    if (toplevel->parent->children_head == toplevel){
        toplevel->parent->children_head = toplevel->next_sibling;
        toplevel->next_sibling = NULL;
        toplevel->parent->children_tail->next_sibling = toplevel;
        toplevel->parent->children_tail = toplevel;
        return;
    }

    ei_widget_t prev_widget = toplevel->parent->children_head;
    ei_widget_t curr_widget = toplevel->parent->children_head->next_sibling;

    while (curr_widget != toplevel){
        prev_widget = prev_widget->next_sibling;
        curr_widget = curr_widget->next_sibling;
    }
    prev_widget->next_sibling = curr_widget->next_sibling;
    toplevel->next_sibling = NULL;
    toplevel->parent->children_tail->next_sibling = toplevel;
    toplevel->parent->children_tail = toplevel;
    return;


}

bool in_rect(ei_point_t point, ei_rect_t rect){
    int left = rect.top_left.x;
    int right = rect.top_left.x + rect.size.width;
    int top = rect.top_left.y;
    int bottom = rect.top_left.y + rect.size.height;
    if ((point.x >= left) && (point.x <=right) && (point.y <= bottom) && (point.y >= top))
        return true;
    return false;
}

ei_point_t* convert_point(ei_anchor_t txt_anchor, ei_rect_t rect, ei_point_t* pts_pt)
{
	ei_point_t top_left = rect.top_left;
	ei_size_t size = rect.size;
	int width = size.width;
	int height = size.height;
	switch(txt_anchor)
	{
		case(ei_anc_none):
			return NULL;
		case(ei_anc_center):
			*pts_pt = (ei_point_t){top_left.x + width/(int)2, top_left.y + height/(int)2};
			break;
		case(ei_anc_east):
			*pts_pt = (ei_point_t){top_left.x + width, top_left.y + height/(int)2};
			break;
		case(ei_anc_north):
			*pts_pt = (ei_point_t){top_left.x + width/(int)2, top_left.y };
			break;
		case(ei_anc_northeast):
			*pts_pt = (ei_point_t){top_left.x + width, top_left.y};
			break;
		case(ei_anc_northwest):
			*pts_pt = top_left;
			break;
		case(ei_anc_west):
			*pts_pt = (ei_point_t){top_left.x, top_left.y + height/(int)2};
			break;
		case(ei_anc_south):
			*pts_pt = (ei_point_t){top_left.x + width/(int)2, top_left.y + height};
			break;
		case(ei_anc_southeast):
			*pts_pt = (ei_point_t){top_left.x + width, top_left.y + height};
			break;
		case(ei_anc_southwest):
			*pts_pt = (ei_point_t){top_left.x, top_left.y + height};
			break;
	}
	return pts_pt;
}