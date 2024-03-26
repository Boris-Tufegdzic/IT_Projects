#include "ei_widget.h"
#include "ei_widgetclass.h"
#include "classes.h"
#include "aux_ei_draw.h"


static uint32_t id_count = 15000;


ei_widget_t		ei_widget_create		(ei_const_string_t	class_name,
							 ei_widget_t		parent,
							 ei_user_param_t	user_data,
							 ei_widget_destructor_t destructor)
{
    ei_widgetclass_t* class = ei_widgetclass_from_name(class_name);

    // class_name doesn't exist
    if(class == NULL) {
        return NULL;
    }

    ei_widget_t new_widget = class->allocfunc();
    new_widget->wclass = class;

    new_widget->user_data = user_data;
    new_widget->destructor = destructor;

    new_widget->parent = parent;

    // updating the parent's hierarchy
    if(parent != NULL) {
        // placing new_children in the linked list
        if (parent->children_tail != NULL) {
            if (parent->children_tail == parent->children_head) {
                parent->children_tail = new_widget;
                parent->children_head->next_sibling = parent->children_tail;
            } else {
                parent->children_tail->next_sibling = new_widget;
                parent->children_tail = new_widget;
            }
        }
        else {
            parent->children_tail = new_widget;
            parent->children_head = new_widget;
        }

        // color_pick creation
        id_count ++;
        new_widget->pick_id = id_count;

        uint8_t* color_ptr = (uint8_t*)(&id_count);
        new_widget->pick_color = (ei_color_t){color_ptr[0], color_ptr[1], color_ptr[2], color_ptr[3]};

    } else {
        return NULL;
    }

    new_widget->children_head = NULL;
    new_widget->children_tail = NULL;
    new_widget->next_sibling = NULL;

    new_widget->placer_params = NULL;

    new_widget->wclass->setdefaultsfunc(new_widget);

    return new_widget;
}


void			ei_widget_destroy		(ei_widget_t		widget)
{
    if(widget != NULL) {

        if(widget->destructor != NULL) {
            widget->destructor(widget);
        }

        ei_widget_t child = widget->children_head;

        while(child != NULL)
        {
            ei_widget_t next_child = child->next_sibling;
            ei_widget_destroy(child);
            child = next_child;
        }

        if(widget->parent != NULL) {

            ei_widget_t parent = widget->parent;

            if(parent->children_head == widget) {
                parent->children_head = widget->next_sibling;
            }
            else {
                ei_widget_t current = parent->children_head;

                while(current->next_sibling != widget) {
                    current = current->next_sibling;
                }

                current->next_sibling = widget->next_sibling;

                if(parent->children_tail == widget) {
                    parent->children_tail = current;
                }
            }
        }

        widget->wclass->releasefunc(widget);
        free(widget);

    }
}


bool	 		ei_widget_is_displayed		(ei_widget_t		widget)
{
    return !(widget->placer_params == NULL);
}


ei_widget_t		ei_widget_pick			(ei_point_t*		where)
{
    return NULL;
}