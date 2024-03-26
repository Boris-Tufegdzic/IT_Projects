#include <stdio.h>
#include <stdlib.h>

#include "ei_application.h"
#include "ei_event.h"
#include "hw_interface.h"
#include "ei_widget_configure.h"


/*
 * ei_main --
 *
 *	Main function of the application.
 */
int main(int argc, char** argv)
{
	ei_widget_t	frame;

	/* Create the application and change the color of the background. */
	ei_app_create((ei_size_t){600, 600}, false);
	ei_frame_set_bg_color(ei_app_root_widget(), (ei_color_t){0x52, 0x7f, 0xb4, 0xff});

	/* Create, configure and place the frame on screen. */
	frame = ei_widget_create	("frame", ei_app_root_widget(), NULL, NULL);
	ei_frame_configure		(frame, &(ei_size_t){300,200},
			   			&(ei_color_t){0x88, 0x88, 0x88, 0xff},
			 			&(int){6},
					 	&(ei_relief_t){ei_relief_raised}, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
	ei_place_xy			(frame, 150, 200);

	/*-------------------------------------------------------------------------------------------------------*/


	ei_surface_t img = hw_image_load("misc/klimt.jpg", ei_app_root_surface());

	ei_rect_t img_rect = hw_surface_get_rect(img);
	ei_rect_ptr_t rect_ptr = &img_rect;


	ei_widget_t toplevel = ei_widget_create("toplevel", ei_app_root_widget(), NULL, NULL);
	ei_place_xy(toplevel, 200, 100);


	ei_widget_t testb = ei_widget_create("button", toplevel, NULL, NULL);
	ei_button_configure(testb, &(ei_size_t){100, 50}, &(ei_color_t){0, 255, 0, 255}, NULL, NULL, &(ei_relief_t){ei_relief_raised}, &(ei_string_t){"Test"}, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
	ei_place(testb, &(ei_anchor_t){ei_anc_center}, NULL, NULL, NULL, NULL, &(float){0.5}, &(float){0.5}, &(float){0.2}, &(float){0.2});


	ei_widget_t toplevel2 = ei_widget_create("toplevel", ei_app_root_widget(), NULL, NULL);
	ei_place_xy(toplevel2, 50, 350);
	ei_toplevel_configure(toplevel2, NULL, NULL, NULL, &(ei_string_t){"Toplevel2"}, &(bool){false}, &(ei_axis_set_t){ei_axis_x}, NULL);


	ei_widget_t frame2 = ei_widget_create("frame", toplevel2, NULL, NULL);
	ei_frame_configure		(frame2, &(ei_size_t){50,50},
			   			&(ei_color_t){0x88, 0x44, 0x33, 0xff},
			 			&(int){6},
					 	&(ei_relief_t){ei_relief_raised}, &(ei_string_t){"Hello !"}, NULL, NULL, NULL, NULL, NULL, NULL);
	ei_place(frame2, &(ei_anchor_t){ei_anc_center}, NULL, NULL, NULL, NULL, &(float){0.5}, &(float){0.5}, &(float){0.5}, &(float){0.5});


	ei_widget_t cut = ei_widget_create("button", ei_app_root_widget(), NULL, NULL);
	ei_button_configure(cut, &(ei_size_t){100, 50}, &(ei_color_t){255, 0, 0, 255}, NULL, NULL, &(ei_relief_t){ei_relief_raised}, &(ei_string_t){"Cut"}, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
	ei_place_xy(cut, 400, 400);


	ei_widget_t frame_img = ei_widget_create("frame", ei_app_root_widget(), NULL, NULL);
	ei_frame_set_image(frame_img, img);
	ei_frame_configure		(frame_img, &(ei_size_t){300,200},
			   			&(ei_color_t){0x88, 0x88, 0x88, 0xff},
			 			&(int){6},
					 	&(ei_relief_t){ei_relief_raised}, NULL, NULL, NULL, NULL, NULL, &rect_ptr, NULL);
	ei_place_xy(frame_img, 0, 0);

	/*-------------------------------------------------------------------------------------------------------*/

	/* Run the application's main loop. */
	ei_app_run();

	/* We just exited from the main loop. Terminate the application (cleanup). */
	ei_app_free();

	hw_surface_free(img);

	return (EXIT_SUCCESS);
}
