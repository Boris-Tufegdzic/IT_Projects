#include <string.h>
#include "ei_widgetclass.h"
#include "ei_draw.h"
#include "hw_interface.h"
#include "ei_widget_configure.h"
#include "ei_types.h"
#include "classes.h"


static ei_widgetclass_t* classes = NULL;


size_t		ei_widget_struct_size()
{
    return sizeof(ei_impl_widget_t);
}


void			ei_widgetclass_register		(ei_widgetclass_t* widgetclass)
{
    if(classes == NULL) {
        classes = widgetclass;
    }
    else {
        ei_widgetclass_t* current = classes;

        while(current->next != NULL) {
            current = current->next;
        }

        current->next = widgetclass;
    }
}


ei_widgetclass_t*	ei_widgetclass_from_name	(ei_const_string_t name)
{
    ei_widgetclass_t* current = classes;

    while((current != NULL) && (strcmp(current->name, name) != 0)) {
        current = current->next;
    }

    return current;
}