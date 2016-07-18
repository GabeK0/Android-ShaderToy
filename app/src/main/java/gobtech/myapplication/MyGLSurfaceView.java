package gobtech.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;

/**
 * Created by Gabe K on 7/15/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {


    private int width, height;
    private MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);


        renderer = new MyGLRenderer();
        setRenderer(renderer);

    }

    public MyGLSurfaceView(Context context, int width, int height) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);

        Point size = new Point();
        this.width = width;
        this.height = height;

        renderer = new MyGLRenderer();
        // Set the renderer to our demo renderer, defined below.
        setRenderer(renderer);
    }
}
