package control;

import Solid.*;
import Zbuffer.ZBuffer;
import raster.Raster;
import transforms.*;
import view.Panel;
import view.Render;

import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

public class Controller3D implements Controller {
    private final Panel panel;
    private boolean switched = false;
    private boolean filled = true;
    private boolean textured = false;
    private Camera cam;
    final double krok_kamery = 0.1;
    private final ArrayList<Solid> sceneBuff = new ArrayList<Solid>();
    private Mat4 modelMat;
    private Mat4 projecMat4;
    private int activeGeometry = 0;
    ZBuffer bf;
    Render render;
    public Controller3D(Panel panel) throws IOException {
        this.panel = panel;
        initObjects(panel.getRaster());
        prepMat();
        initScene();
        initListeners();
        prepZBuff();
        show();
    }

    public void initObjects(Raster<Col> raster) {
        raster.setDefaultValue(new Col(0x101010));
    }

    //pridam objekty do sceny
    public void initScene() throws IOException {
        sceneBuff.add(new Triangle());
        sceneBuff.add(new TestTriangle());
        sceneBuff.add(new BicubicObject(Cubic.FERGUSON));
        //sceneBuff.add(new ObjLoader("U:\\grafika-Projekty\\pgrf2-2024-cv04\src\\object.obj"));
        sceneBuff.add(new Axis());
    }

    public void prepZBuff(){
        bf = new ZBuffer(panel.getRaster());
    }

    private void prepMat() {
        modelMat = new Mat4Identity();

        Vec3D e = new Vec3D(0, 20, 10);
        double azimuth = Math.toRadians(-100);
        double zenith = Math.toRadians(-35);
        cam = createCamera(e, azimuth, zenith);

        double fov = Math.PI / 3;
        double aspectRatio = (double) panel.getRaster().getHeight() / panel.getRaster().getWidth();
        double near = 0.5;
        double far = 150;
        projecMat4 = createPerspectiveProjection(fov, aspectRatio, near, far);
    }

    private Camera createCamera(Vec3D position, double azimuth, double zenith) {
        return new Camera()
                .withPosition(position)
                .withAzimuth(azimuth)
                .withZenith(zenith);
    }

    private Mat4 createPerspectiveProjection(double fov, double aspectRatio, double near, double far) {
        return new Mat4PerspRH(fov, aspectRatio, near, far);
    }

    @Override
    public void initListeners() {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
        MouseAdapter cameraListener = new MouseAdapter() {
            boolean mouseClicked;
            int x1;
            int y1;
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClicked = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseClicked = true;
                x1 = e.getX();
                y1 = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseClicked) {
                    double a = (e.getY() - y1) / 300d;
                    double b = (e.getX() - x1) / 300d;
                    cam = cam.addAzimuth(b);
                    cam = cam.addZenith(a);
                    show();
                    x1 = e.getX();
                    y1 = e.getY();

                }
            }
        };
        panel.addMouseListener(cameraListener);
        panel.addMouseMotionListener(cameraListener);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_W) {
                    cam = cam.forward(krok_kamery);
                }
                if (key == KeyEvent.VK_S) {
                    cam = cam.backward(krok_kamery);
                }
                if (key == KeyEvent.VK_A) {
                    cam = cam.left(krok_kamery);
                }
                if (key == KeyEvent.VK_D) {
                    cam = cam.right(krok_kamery);
                }
                if (key == KeyEvent.VK_F){
                    if(switched){
                        projecMat4 =  new Mat4PerspRH(Math.PI / 3, bf.getImageBuffer().getHeight() / (float) bf.getImageBuffer().getWidth(), 0.6, 30);
                    }
                    else{
                        projecMat4 = new Mat4OrthoRH(5, 5, 0.5, 150);
                    }
                    switched = !switched;

                }
                if(key == KeyEvent.VK_G){
                    filled = !filled;
                }
                if(key == KeyEvent.VK_O){
                    if(activeGeometry + 1 > sceneBuff.size() - 2){
                        return;
                    }
                    else{
                        activeGeometry += 1;
                    }
                }
                if(key == KeyEvent.VK_I){
                    if(activeGeometry -1 < 0 ){
                        return;
                    }
                    else{
                        activeGeometry -= 1;
                    }
                }

                if(key == KeyEvent.VK_UP){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Transl(0,1,0)));
                }
                if(key == KeyEvent.VK_DOWN){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Transl(0,-1,0)));
                }
                if(key == KeyEvent.VK_LEFT){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Transl(1,0,0)));
                }
                if(key == KeyEvent.VK_RIGHT){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Transl(-1,0,0)));
                }
                if(key == KeyEvent.VK_X){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4RotXYZ(.5d,0d,0d)));
                }
                if(key == KeyEvent.VK_Y){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4RotXYZ(0d,.5d,0d)));
                }
                if(key == KeyEvent.VK_Z){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4RotXYZ(0d,0d,.5d)));
                }
                if(key == KeyEvent.VK_N){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Scale(.5d,.5d,.5d)));
                }
                if(key == KeyEvent.VK_M){
                    sceneBuff.get(activeGeometry).setModel(sceneBuff.get(activeGeometry).getModel().mul(new Mat4Scale(1.5d,1.5d,1.5d)));
                }
                if(key == KeyEvent.VK_T){
                    textured = !textured;
                }
                show();
            }
        });
    }

    private void show() {
        panel.clear();
        bf.clear();
        bf.getImageBuffer().clear();
        render = new Render(bf, modelMat,cam.getViewMatrix(),projecMat4);
        render.setTextured(textured);
        render.setFilled(filled);
        render.clear();
        render.draw(sceneBuff);
        panel.repaint();
    }
}