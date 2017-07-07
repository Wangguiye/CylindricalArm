package cylindricalarm;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
import javax.swing.JFrame;
import java.awt.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.vecmath.Point3d;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.util.Timer;
import java.util.TimerTask;
import static javax.media.j3d.Appearance.ALLOW_TEXTURE_WRITE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3d;

public class CylindricalArm extends Applet implements ActionListener, KeyListener{
    
    private BranchGroup scene;
    
    private static Transform3D handlePosition;
    private static TransformGroup handleMovement;
    private static Transform3D armPosition;
    private static TransformGroup armMovement;
    private static Transform3D handPosition;
    private static TransformGroup handMovement;
    private static Transform3D hand1Position;
    private static TransformGroup hand1Movement;
    private static Transform3D hand2Position;
    private static TransformGroup hand2Movement;
    private static TransformGroup rotation;
    private static Transform3D rotate;   
    private static Transform3D ballRotation;
    private static TransformGroup ballRotate;
    
    private static Transform3D ballPosition;
    private static TransformGroup ballMovement;
    
    //Camera movement
    private TransformGroup cameraMovement;
    private Transform3D cameraPosition;    
    private TransformGroup cameraRotation;
    
    //Arm Handle
    private static final float handleSizeX = 0.2f;
    private final float handleSizeY = 0.03f;
    private final float handleSizeZ = 0.1f;
    private static final float handleGapY = 0.2f;
    private static final float handlePosX = handleSizeX/3;
    private final float handleMaxX = handleSizeX/2 + handlePosX;
    
    //Core 
    private final float coreRadius = 0.05f;
    private final float coreHeight = 0.8f;
    
    //Arm
    private final float armRadius = 0.02f;
    private static final float armLenght = 0.5f;
    
    private final float maxY = coreHeight;
    private final int minY = 0;
    private final float maxX = handleSizeX+handlePosX + armLenght/2;
    private static final float minX = handlePosX;
    private static final float armGapX = 0.05f;
    
    //Hand
    private static final float handWidth = 0.05f;
    private static final float handMainX = 0.03f;
    private final float handMainY = 0.02f;
    private final float fingersLenght = 0.05f;
    private final float fingersWidth =  0.02f;
    private final float fingersHeight = 0.01f;
    
    //Ground
    private final float floorRadius = 0.8f;
    private final float floorHeight = 0.01f;
    
    //Lift
    private final float liftLenght = 0.5f;
    private final float liftWidth =  0.1f;
    private final float liftHeight = 0.01f;    
    private static final float liftAngle = (float)Math.PI/2;
    private static final float liftPose = 0.5f;
    private static float liftDistance = 0.0f;
    
    //Base
    private final float baseSize = 0.2f;
    private final float baseHeight = 0.03f;
    
    private final Vector3f liftVector = new Vector3f(-0.4f,0.01f,liftPose);
    
    //Textures
    private final float liftTextureHeight = 2*liftHeight +0.001f;    
    private final float baseTextureHeight = 2*baseHeight +0.001f;
    
    //Ranges
    private final float maxRadius = 0.8f;
    private final float minRadius = 0.3f;
    private final float maxHeight = 0.7f;
    private final float minHeight = 0.2f;
    
    private static float handPose; 
    private static float ballPoseX;
    private static float ballPoseY;
    private double distance;
    private final float distTolerance = 0.05f;
    private static float destAngle;
    private static final float angTolerance = 0.005f;
    private final float heightTolerance = 0.01f;
    private float ballAngle;
    private static float handlePosY;
    private static float armPosX = handlePosX + handleSizeX;    
    private static float angle;    
    private float camPosY = 2.5f;
    private float camPosX = 0.0f;
    private float camPosZ = 2.5f;
    private float startCamPosY = 4.0f;
    private float startCamPosX = 1.5f;
    private float startCamPosZ = 4.0f;    
    
    //Buttons & Fields
    private static  JButton placeBallButton;
    private static  JButton pickBallButton;
    private static  JButton collectBallButton;
    private static  JButton startButton;
    private static  JTextField xField;
    private static  JTextField yField;
    private static  JTextField zField;
    
    JPanel startPanel;
    JPanel rootPanel;
    JPanel controlPanel;    
    
    private GraphicsConfiguration config=null;
    private Canvas3D canvas=null;
    private SimpleUniverse universe=null;
    
    //Camera vectors
    public  static Point3d viewersLocation;
    private Vector3d viewVector;
    public  final  Point3d gazePoint = new Point3d(0,0.4,0);
    private final Vector3d upVector= new Vector3d(0,1,0);   

    Sphere kulka; 
    
    //Lift textures
    private static ImageComponent2D image1;
    private static ImageComponent2D image2;
    private static boolean change = true;
    private static Texture2D tasma;
    private static Appearance liftAppearance;
    private static Shape3D lift;
    
    //Variables controlling state of robot
    private boolean pickBall;
    private boolean collectBall;
    private boolean moveBall;
    private boolean armSet;
    private boolean clockwise;
    private boolean ballOperation;
    private boolean moveCamera;
    private boolean cameraOperation;
    
    
    public CylindricalArm(){
        JFrame Window = new JFrame("Cylindrical Arm");
        Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Window.setResizable(false);
        
        this.config = SimpleUniverse.getPreferredConfiguration();
        this.canvas = new Canvas3D(config);
        this.canvas.setPreferredSize(new Dimension(800,600));
        this.canvas.addKeyListener(this);
        
        rootPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        controlPanel = new JPanel();
        rootPanel.setVisible(true);
        
        startPanel = new JPanel();
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100,30));
        startButton.addActionListener(this);
        startPanel.add(startButton);
        
        controlPanel.setVisible(false);
        startPanel.setVisible(true);
        
        xField = new JTextField("0.5");
        xField.setPreferredSize(new Dimension(60,20));
        yField = new JTextField("0.5");
        yField.setPreferredSize(new Dimension(60,20));
        zField = new JTextField("0.5");
        zField.setPreferredSize(new Dimension(60,20));
        placeBallButton = new JButton("Place ball");
        placeBallButton.setPreferredSize(new Dimension(100,30));
        placeBallButton.addActionListener(this);
        placeBallButton.setEnabled(false);
        pickBallButton = new JButton("Pick ball");
        pickBallButton.setPreferredSize(new Dimension(100,30));
        pickBallButton.addActionListener(this);
        pickBallButton.setEnabled(false);
        collectBallButton = new JButton("Collect ball");
        collectBallButton.setPreferredSize(new Dimension(100,30));
        collectBallButton.addActionListener(this);
        collectBallButton.setEnabled(false);
        
        controlPanel.add(new JLabel("PosX:"));
        controlPanel.add(xField);
        controlPanel.add(new JLabel("PosY:"));
        controlPanel.add(yField);
        controlPanel.add(new JLabel("PosZ:"));
        controlPanel.add(zField);
        controlPanel.add(placeBallButton);
        controlPanel.add(pickBallButton);
        controlPanel.add(collectBallButton);
        
        buttonPanel.add(startPanel, BorderLayout.NORTH);
        buttonPanel.add(controlPanel, BorderLayout.SOUTH);
        
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);   
        rootPanel.add(canvas, BorderLayout.CENTER);       

        Window.add(rootPanel);
        Window.setVisible(true);
        Window.pack();           
        
        scene = CreateScene();        
            
        viewVector = new Vector3d(camPosX,camPosY,camPosZ);
        
        universe = new SimpleUniverse(canvas); 
        universe.addBranchGraph(scene);    
        
        OrbitBehavior orbit = new OrbitBehavior(canvas);
        orbit.setSchedulingBounds(new BoundingSphere());
        universe.getViewingPlatform().setViewPlatformBehavior(orbit);
        
        universe.getViewingPlatform().setNominalViewingTransform();
        
        viewersLocation = new Point3d(startCamPosX,startCamPosY,startCamPosZ);        
        
        cameraPosition = new Transform3D();
        cameraPosition.lookAt(viewersLocation, gazePoint, upVector);
        cameraPosition.invert();
        cameraMovement= this.universe.getViewingPlatform().getViewPlatformTransform(); 
        cameraMovement.setTransform(cameraPosition);
        
        rotate = new Transform3D();
        angle = 0.0f;  
        
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Refreshing(),0,10);
        
        pickBall = false;
        collectBall = false;
        moveBall = false;
        ballOperation = false;
        moveCamera = false;
        cameraOperation = true;
    }
    
    
    public BranchGroup CreateScene(){
        BranchGroup scene_node = new BranchGroup();
        
        BoundingSphere bounds = new BoundingSphere();
        
        //Lights
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setInfluencingBounds(bounds);
        scene_node.addChild(ambientLight);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setInfluencingBounds(bounds);
        directionalLight.setDirection(new Vector3f(0.0f, 0.0f,-1.0f));
        directionalLight.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        scene_node.addChild(directionalLight);

        //Ground      
        Appearance platformAppearance = new Appearance();
        platformAppearance.setColoringAttributes(new ColoringAttributes(0.9f,0.9f,0.9f,ColoringAttributes.NICEST)); 

        Cylinder Podloga = new Cylinder(floorRadius, floorHeight, platformAppearance);
        Transform3D floorPosition = new Transform3D();       
        floorPosition.setTranslation(new Vector3f(0.0f,-0.005f, -0.0f));
        TransformGroup floorMovement = new TransformGroup();
        floorMovement.addChild(Podloga);
        floorMovement.setTransform(floorPosition);
        scene_node.addChild(floorMovement);
        
        //Arm Handle
        handlePosY=0.5f;    
        TextureLoader loader = new TextureLoader("pictures/handle.png",null);
        ImageComponent2D image = loader.getImage();
        Texture2D handleTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        handleTexture.setImage(0, image);
        handleTexture.setBoundaryModeS(Texture.WRAP);
        handleTexture.setBoundaryModeT(Texture.WRAP);
        Appearance handleAppearance = new Appearance();
        handleAppearance.setTexture(handleTexture);
        Box handle = new Box(handleSizeX,handleSizeY,handleSizeZ,Box.GENERATE_TEXTURE_COORDS,handleAppearance);

        handlePosition = new Transform3D();
        handlePosition.setTranslation(new Vector3f(handleSizeX/3,handlePosY,0.0f));
        handleMovement = new TransformGroup();
        handleMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        handleMovement.setTransform(handlePosition);
        handleMovement.addChild(handle);       
                
        rotation = new TransformGroup();
        rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotation.addChild(handleMovement);
        
        //Core
         loader = new TextureLoader("pictures/metal_core.png",null);
         image = loader.getImage();
        Texture2D coreTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        coreTexture.setImage(0, image);
        coreTexture.setBoundaryModeS(Texture.WRAP);
        coreTexture.setBoundaryModeT(Texture.WRAP);
        
        Appearance coreAppearance = new Appearance();
        coreAppearance.setTexture(coreTexture);

        Cylinder core = new Cylinder(coreRadius, coreHeight,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, coreAppearance);

        Transform3D corePosition = new Transform3D();
        corePosition.setTranslation(new Vector3f(0.0f,0.4f,0.0f));
        TransformGroup coreMovement = new TransformGroup();
        coreMovement.setTransform(corePosition);
        coreMovement.addChild(core);

        scene_node.addChild(coreMovement);       
        
        //Base
        loader = new TextureLoader("pictures/base_sides1.png",null);
        image = loader.getImage();
        Texture2D baseSidesTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        baseSidesTexture.setImage(0, image);
        baseSidesTexture.setBoundaryModeS(Texture.WRAP);
        baseSidesTexture.setBoundaryModeT(Texture.WRAP);
        Appearance baseAppearance = new Appearance();
        baseAppearance.setTexture(baseSidesTexture);
        //baseAppearance.setColoringAttributes(new ColoringAttributes(0.61f,0.65f,0.69f,ColoringAttributes.NICEST));
        Box base = new Box(baseSize,baseHeight,baseSize,Box.GENERATE_TEXTURE_COORDS,baseAppearance);
        Transform3D basePosition = new Transform3D();
        basePosition.setTranslation(new Vector3d(0,baseHeight,0));
        TransformGroup baseMovement = new TransformGroup(basePosition);
        baseMovement.addChild(base);
        scene_node.addChild(baseMovement);
        
        loader = new TextureLoader("pictures/base1.png",null);
        image = loader.getImage();
        Texture2D baseTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());        
        baseTexture.setImage(0, image);
        baseTexture.setBoundaryModeS(Texture.WRAP);
        baseTexture.setBoundaryModeT(Texture.WRAP);
        
        Point3f[]  baseCoords ={new Point3f(),new Point3f(),new Point3f(),new Point3f()};        
        Point2f[]  baseTextureCoords = { new Point2f(),new Point2f(),new Point2f(),new Point2f()};
        
        baseCoords[0].y = baseTextureHeight;
        baseCoords[1].y = baseTextureHeight;
        baseCoords[2].y = baseTextureHeight;
        baseCoords[3].y = baseTextureHeight;
        
        baseCoords[0].x = baseSize;
        baseCoords[1].x = baseSize;
        baseCoords[2].x = -baseSize;
        baseCoords[3].x = -baseSize;

        baseCoords[0].z = baseSize;
        baseCoords[1].z = -baseSize;
        baseCoords[2].z = -baseSize;
        baseCoords[3].z = baseSize;     

        baseTextureCoords[0].x = 1.0f;
        baseTextureCoords[0].y = 0.0f;

        baseTextureCoords[1].x = 1.0f;
        baseTextureCoords[1].y = 1.0f;

        baseTextureCoords[2].x = 0.0f;
        baseTextureCoords[2].y = 1.0f;

        baseTextureCoords[3].x = 0.0f;
        baseTextureCoords[3].y = 0.0f;

        QuadArray qa_base = new QuadArray(4, GeometryArray.COORDINATES| GeometryArray.TEXTURE_COORDINATE_2);
        qa_base.setCoordinates(0,baseCoords);
        qa_base.setTextureCoordinates(0, baseTextureCoords);        
        
        Appearance baseTextureAppearance = new Appearance();
             
        baseTextureAppearance.setTexture(baseTexture);
        Shape3D shape_base = new Shape3D(qa_base);
        shape_base.setAppearance(baseTextureAppearance);              
        
        scene_node.addChild(shape_base);
        
        //Arm
         
        loader = new TextureLoader("pictures/metal_arm.png",null);
        image = loader.getImage();
        Texture2D armTexture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        armTexture.setImage(0, image);
        armTexture.setBoundaryModeS(Texture.WRAP);
        armTexture.setBoundaryModeT(Texture.WRAP);
        Appearance armAppearance = new Appearance();
        armAppearance.setTexture(armTexture);
        Cylinder arm = new Cylinder(armRadius, armLenght,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, armAppearance);

        armPosition = new Transform3D();
        armPosition.rotZ(Math.PI/2);
        armPosition.setTranslation(new Vector3f(armPosX,handlePosY,0.0f));

        armMovement = new TransformGroup();
        armMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        armMovement.setTransform(armPosition);
        armMovement.addChild(arm);       

        rotation.addChild(armMovement);
        
        //Hand
        Appearance handAppearance = new Appearance();
        handAppearance.setColoringAttributes(new ColoringAttributes(0.6f,0.6f,0.8f,ColoringAttributes.NICEST));

        //handAppearance.setTexture(handTexture);
        Box hand = new Box(handMainX, handMainY,handWidth,Box.GENERATE_TEXTURE_COORDS,baseAppearance);

        handPose =armPosX+armLenght/2;
        handPosition = new Transform3D();
        handPosition.setTranslation(new Vector3f(handPose,handlePosY,0.0f));
        handMovement = new TransformGroup();
        handMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        handMovement.setTransform(handPosition);
        handMovement.addChild(hand);

        rotation.addChild(handMovement);

        Box hand1 = new Box(fingersLenght, fingersWidth,fingersHeight,Box.GENERATE_TEXTURE_COORDS, baseAppearance);

        hand1Position = new Transform3D();
        hand1Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,handWidth));
        hand1Movement = new TransformGroup();
        hand1Movement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        hand1Movement.setTransform(hand1Position);
        hand1Movement.addChild(hand1);

        rotation.addChild(hand1Movement);

        Box hand2 = new Box(fingersLenght, fingersWidth,fingersHeight,Box.GENERATE_TEXTURE_COORDS, baseAppearance);

        hand2Position = new Transform3D();
        hand2Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,-handWidth));
        hand2Movement = new TransformGroup();
        hand2Movement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        hand2Movement.setTransform(hand2Position);
        hand2Movement.addChild(hand2);

        rotation.addChild(hand2Movement);
     
        //Lift             
                
        Appearance liftBoxAppearance = new Appearance();
        liftBoxAppearance.setColoringAttributes(new ColoringAttributes(0.2f,0.2f,0.2f,ColoringAttributes.NICEST));
        Box liftBox = new Box(liftLenght,liftHeight,liftWidth,liftBoxAppearance);

        Transform3D liftPosition = new Transform3D();
      
        liftPosition.setTranslation(new Vector3f(-0.4f,0.01f,liftPose));
        TransformGroup liftMovement = new TransformGroup();
        liftMovement.setTransform(liftPosition);
        liftMovement.addChild(liftBox);
        scene_node.addChild(liftMovement);  
        
        TextureLoader loader1 = new TextureLoader("pictures/rubber3.png",null);
        image1 = loader1.getImage();
        TextureLoader loader2 = new TextureLoader("pictures/rubber4.png",null);
        image2 = loader2.getImage();
        tasma = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image1.getWidth(), image1.getHeight());

        tasma.setImage(0, image1);
        tasma.setBoundaryModeS(Texture.WRAP);
        tasma.setBoundaryModeT(Texture.WRAP);

        Point3f[]  coords ={new Point3f(),new Point3f(),new Point3f(),new Point3f()};        
        Point2f[]  tex_coords = { new Point2f(),new Point2f(),new Point2f(),new Point2f()};
        
        coords[0].y = liftTextureHeight;
        coords[1].y = liftTextureHeight;
        coords[2].y = liftTextureHeight;
        coords[3].y = liftTextureHeight;
        
        coords[0].x = liftLenght + liftVector.x;
        coords[1].x = liftLenght + liftVector.x;
        coords[2].x = -liftLenght + liftVector.x;
        coords[3].x = -liftLenght + liftVector.x;

        coords[0].z = liftWidth + liftVector.z;
        coords[1].z = -liftWidth + liftVector.z;
        coords[2].z = -liftWidth + liftVector.z;
        coords[3].z = liftWidth + liftVector.z;        

        tex_coords[0].x = 1.0f;
        tex_coords[0].y = 0.0f;

        tex_coords[1].x = 1.0f;
        tex_coords[1].y = 1.0f;

        tex_coords[2].x = 0.0f;
        tex_coords[2].y = 1.0f;

        tex_coords[3].x = 0.0f;
        tex_coords[3].y = 0.0f;

        QuadArray qa_tasma = new QuadArray(4, GeometryArray.COORDINATES| GeometryArray.TEXTURE_COORDINATE_2);
        qa_tasma.setCoordinates(0,coords);
        qa_tasma.setTextureCoordinates(0, tex_coords);        
        
        liftAppearance = new Appearance();
             
        liftAppearance.setTexture(tasma);
        liftAppearance.setCapability(ALLOW_TEXTURE_WRITE);
        lift = new Shape3D(qa_tasma);
        lift.setAppearance(liftAppearance);
        
        TransformGroup liftTextureMovement = new TransformGroup();
        Transform3D liftTexturePosition = new Transform3D();
        liftTextureMovement.addChild(lift);
        liftTextureMovement.setTransform(liftTexturePosition);        
        
        scene_node.addChild(liftTextureMovement);    
      
        scene_node.addChild(rotation);
      
        return scene_node;       
    }
    
    public static void SwitchImages(){
        Texture2D img;        
        if(change){
            img = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image1.getWidth(), image1.getHeight());
            img.setImage(0,image1);
            change = false;
        }
        else{
            img = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image2.getWidth(), image2.getHeight());
            img.setImage(0,image2);
            change = true;
        }
        liftAppearance.setTexture(img);
    }
    
    @Override
       public void actionPerformed(ActionEvent e){
        if(e.getSource()== placeBallButton){
            float posX = Float.parseFloat(xField.getText());
            float posY = Float.parseFloat(yField.getText());
            float posZ = Float.parseFloat(zField.getText());
            
            float radius =(float)Math.sqrt(posX*posX+posZ*posZ);
            
            if(radius < maxRadius && radius > minRadius && posY < maxHeight && posY > minHeight){
                PlaceBall(posX,posY,posZ);
            }
        }
        else if(e.getSource()== pickBallButton){
            SetDestAngle(ballAngle);
            CheckClockwise();
            armSet = false;
            pickBall = true;            
        }
        else if(e.getSource()== collectBallButton){
            SetDestAngle(liftAngle);
            CheckClockwise();
            armSet = false;
            pickBall = false;
            collectBall = true;
        }
        else if(e.getSource()== startButton){
            controlPanel.setVisible(true);
            startPanel.setVisible(false);
            moveCamera = true;
        }
    }     
       
    public void PlaceBall(float posx, float posy, float posz){
        float posX = posx;
        float posY = posy;
        float posZ = posz;
           
        destAngle = GetAngle(posX,posZ);
        ballAngle = destAngle;
        BranchGroup branch = new BranchGroup();
        Appearance ballAppearance = new Appearance();
        ballAppearance.setColoringAttributes(new ColoringAttributes(1.0f,0.0f,0.0f,ColoringAttributes.NICEST));
            
        kulka = new Sphere(0.035f,ballAppearance);
            
        ballPoseX = (float)Math.sqrt(posX*posX+posZ*posZ);
        ballPoseY = posY;
            
        Vector3d vector = new Vector3d(ballPoseX,ballPoseY,0.0);
        ballPosition = new Transform3D();
        ballPosition.setTranslation(vector);
        ballMovement = new TransformGroup();
        ballMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ballMovement.setTransform(ballPosition);
        ballMovement.addChild(kulka);
            
        ballRotation = new Transform3D();
        ballRotation.rotY(-ballAngle);
        ballRotate = new TransformGroup();
        ballRotate.setTransform(ballRotation);
        ballRotate.addChild(ballMovement);
        ballRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        branch.addChild(ballRotate);
        universe.addBranchGraph(branch);            
  
        pickBallButton.setEnabled(true);
        placeBallButton.setEnabled(false);
        ballOperation = true;
    }
   
    public float GetAngle(float posX, float posZ){
        float angle = (float)(Math.atan2(posZ,posX));        
        if (angle<0) angle =(float)(2*Math.PI + angle);       
        return angle;
    }
    
    public void CheckClockwise(){
        boolean _clockwise = true;
        if((destAngle > angle && destAngle - angle > Math.PI) || (destAngle<angle && angle - destAngle <Math.PI)) _clockwise = false;        
        clockwise = _clockwise;
    }
    
    public void PickBall(){          
            if(armPosX > minX+armGapX && !armSet){
                handPose-= 0.002;
                armPosX -= 0.002;
            }
            else if(Math.abs(angle-destAngle) > angTolerance){
                if(clockwise){
                    if(angle>=2*Math.PI) angle = 0;
                    angle +=0.01;
                }
                else{
                    if(angle<=0) angle = (float)(2*Math.PI);
                    angle -=0.01;
                }
                armSet = true;
            }
            else if (Math.abs(handlePosY-ballPoseY) > heightTolerance){
                if(handlePosY > ballPoseY) handlePosY-=0.002;
                else handlePosY +=0.002;
                armSet = true;
            }
            else if(Math.abs(ballPoseX-handPose) > distTolerance){
                if(handPose < ballPoseX){
                    armPosX +=0.002;
                    handPose +=0.002;
                    armSet = true;
                }                            
            }
            else{
                pickBallButton.setEnabled(false);
                collectBallButton.setEnabled(true);                
            } 
    }
    
    public void CollectBall(){            
            if(armPosX > minX+armGapX && !armSet){
                handPose-= 0.002;
                armPosX -= 0.002;
                ballPoseX-=0.002;   
            }            
            else if(Math.abs(angle-destAngle) > angTolerance){
                if(clockwise){
                    if(angle>=2*Math.PI) angle = 0;
                    angle +=0.01;
                }
                else{
                    if(angle<=0) angle = (float)(2*Math.PI);
                    angle -=0.01;
                }                
                rotate.rotY(-angle);
                rotation.setTransform(rotate);
                armSet = true;
            }
            else if (handlePosY > handleGapY){
                handlePosY-=0.002;
                ballPoseY-=0.002;       
                armSet = true;
            }   
            else if(handPose< liftPose-0.03){                
                armPosX +=0.002;
                ballPoseX+=0.002;
                handPose +=0.002;                
                armSet = true;
            }                     
            else{
                collectBallButton.setEnabled(false);                
                moveBall = true;
                collectBall = false;
                ballOperation = false;                
            }
    }
    
    public static void SetDestAngle(float angle){                
        destAngle = angle;
    }
    
    public void MoveBall(){        
            if(ballPoseY > 0.06){
            ballPoseY -=0.006;        
        }
        else if(liftDistance<0.9){
            liftDistance+=0.003;
        }
        else{
             ballPoseY = 100.0f;
             moveBall = false;
             SwitchButtons();
             liftDistance = 0;
             placeBallButton.setEnabled(true);
        }   
    }
    
    public static void SwitchButtons(){                
        collectBallButton.setEnabled(false);
        pickBallButton.setEnabled(false);
        placeBallButton.setEnabled(true);
    }
    
    public void MoveCamera(){         
            if(viewersLocation.x > camPosX || viewersLocation.y > camPosY || viewersLocation.z > camPosZ){
                if(viewersLocation.x > camPosX) viewersLocation.x -=0.006;
                if(viewersLocation.y > camPosY) viewersLocation.y -=0.006;
                if(viewersLocation.z > camPosZ) viewersLocation.z -=0.006;                
                
                cameraPosition.lookAt(viewersLocation, gazePoint, upVector);           
                cameraPosition.invert(); 
                cameraMovement.setTransform(cameraPosition);
            }
            else{
                placeBallButton.setEnabled(true);
                moveCamera = false;
                cameraOperation = false;
            }    
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        if(!ballOperation && !cameraOperation){
            if(e.getKeyCode() == KeyEvent.VK_UP){
                if(handlePosY < maxY-handleGapY) handlePosY += 0.004;
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                if(handlePosY > minY + handleGapY) handlePosY -= 0.004;
            }
            if(e.getKeyChar() == 'z'){
                if(armPosX < maxX-armGapX){
                    handPose+= 0.004;
                    armPosX += 0.004;
                }
            }
            else if(e.getKeyChar() == 'x'){
                if(armPosX > minX+armGapX){
                    handPose-= 0.004;
                    armPosX -= 0.004;
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                if(angle >= 2*Math.PI) angle = 0;
                angle +=0.05;
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                if(angle <= 0) angle = (float)(2*Math.PI); 
                angle -=0.05;
            }   
        } 
        if(!cameraOperation){
            if(e.getKeyChar() == 'w'){
                viewVector.y += 0.020;
            }
            else if(e.getKeyChar() == 's'){
                viewVector.y -= 0.020;
            }
            if(e.getKeyChar() == 'd'){
                viewVector.x += 0.025;
            }
            else if(e.getKeyChar() == 'a'){
                viewVector.x -= 0.025;
            }
             if(e.getKeyChar() == 'q'){
                viewVector.z -= 0.016;
            }
            else if(e.getKeyChar() == 'e'){
                viewVector.z += 0.016;
            }
        }
        
        handlePosition.setTranslation(new Vector3f(handlePosX,handlePosY,0.0f));
        handleMovement.setTransform(handlePosition);
        armPosition.setTranslation(new Vector3f(armPosX,handlePosY,0.0f));
        armMovement.setTransform(armPosition);
        handPosition.setTranslation(new Vector3f(handPose,handlePosY,0.0f));
        handMovement.setTransform(handPosition);
        hand1Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,handWidth));
        hand1Movement.setTransform(hand1Position);
        hand2Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,-handWidth));
        hand2Movement.setTransform(hand2Position);
        rotate.rotY(-angle);
        rotation.setTransform(rotate);
        if(!cameraOperation){
            viewersLocation = new Point3d(viewVector.x,viewVector.y,viewVector.z);  
            cameraPosition.lookAt(viewersLocation, gazePoint, upVector);
            cameraPosition.invert();
            cameraMovement.setTransform(cameraPosition);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    class Refreshing extends TimerTask
    {
        private boolean finished = false;
        private int stage=0;
        private long timerValue = 0;         
        
        @Override
        public void run()
        {
           if(timerValue%20==0 && !cameraOperation) SwitchImages();
           if(moveCamera){
               MoveCamera();
           }
           else if(pickBall || collectBall || moveBall){
               if(pickBall) PickBall();
               else if(collectBall || moveBall){
                    if(collectBall){
                        CollectBall();                        
                        ballRotation.rotY(-angle);
                        ballRotate.setTransform(ballRotation);
                    }
                    else MoveBall();
                    ballPosition.setTranslation(new Vector3d(ballPoseX,ballPoseY,liftDistance));
                    ballMovement.setTransform(ballPosition);
               }               
                handlePosition.setTranslation(new Vector3f(handlePosX,handlePosY,0.0f));
                handleMovement.setTransform(handlePosition);
                armPosition.setTranslation(new Vector3f(armPosX,handlePosY,0.0f));
                armMovement.setTransform(armPosition);
                handPosition.setTranslation(new Vector3f(armPosX+armLenght/2,handlePosY,0.0f));
                handMovement.setTransform(handPosition);
                hand1Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,handWidth));
                hand1Movement.setTransform(hand1Position);
                hand2Position.setTranslation(new Vector3f(armPosX+armLenght/2 + handMainX,handlePosY,-handWidth));
                hand2Movement.setTransform(hand2Position);
                rotate.rotY(-angle);
                rotation.setTransform(rotate);               
           }                
              timerValue++;     
        }        
    }   
    
    public static void main(String[] args) throws InterruptedException {
        CylindricalArm ramie = new CylindricalArm();
        ramie.addKeyListener(ramie);        
    }    
}
