/* 
 * Copyright 2011 by Mark Coletti, Keith Sullivan, Sean Luke, and
 * George Mason University Mason University Licensed under the Academic
 * Free License version 3.0
 *
 * See the file "LICENSE" for more information
 *
 * $Id: ColorWorld.java 841 2012-12-18 00:50:15Z mcoletti $
 */
package SimpactII.GeoTests;

import SimpactII.SimpactII;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomGridField;
import sim.field.geo.GeomGridField.GridDataType;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ArcInfoASCGridImporter;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.AttributeValue;
import sim.util.geo.MasonGeometry;

/**
 * The ColorWorld example shows how to change the portrayal of individual
 * geometries based on simulation information. To do this, we create our own
 * portrayal and MasonGeometry. The portrayal accesses the simulation core via
 * the extended MasonGeometry class.
 *
 * This simulation has agents wandering randomly around Fairfax County, VA,
 * voting districts. There are 12 districts total. The color of the district
 * changes shade based on the number of agents currently inside the district. If
 * no agents are inside the district, then the district is not shown (actually,
 * its drawn as white, on a white background).
 *
 * There are two special things about this simulation: First, we subclass
 * MasonGeometry to count the number of agents inside each district, and use
 * this subclass as a replacement for the standard MasonGeometry. The
 * replacement *MUST* be done prior to ingesting the files to ensure that the
 * GeomField uses our subclass rather than the standard MasonGeometry. Second,
 * we use the global Union of the voting districts to determine if the agents
 * are wandering out of the county. Doing this instead of looping through all
 * the districts provides at least an order of magnitude speedup. We also
 * compute the ConvexHull, mainly to show how its done.
 *
 */
public class GeoMasonTest extends SimState {

    private static final long serialVersionUID = -2568637684893865458L;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    // number of agents in the simulation
    public static int NUM_AGENTS = 0;
    
    // where all the county geometry lives
    public GeomVectorField magisterialDistricts = new GeomVectorField(WIDTH, HEIGHT); 
    public GeomVectorField agents = new GeomVectorField(WIDTH, HEIGHT);
    public GeomVectorField spots = new GeomVectorField(WIDTH,HEIGHT); //http://www.gap.csir.co.za/download-maps-and-data

    // getters and setters for inspectors
    public int getNumAgents() {
        return NUM_AGENTS;
    }

    public void setNumAgents(int a) {
        if (a > 0) {
            NUM_AGENTS = a;
        }
    }
    
    public GeoMasonTest(){
        this(System.currentTimeMillis());
    }

    public GeoMasonTest(long seed) {
        super(seed);

        // this line allows us to replace the standard MasonGeometry with our
        // own subclass of MasonGeometry; see CountingGeomWrapper.java for more info.
        // Note: this line MUST occur prior to ingesting the data
        URL magisterialBoundaries = SimpactII.class.getResource("Migration/ZAF_adm2.shp");
        URL populationBoundaries = SimpactII.class.getResource("Migration/meso_2010_pop_dd.shp");
        //InputStream inputStream = SimpactII.class.getResourceAsStream("Data/zaf_pop.gri");
        
        //URL pop = SimpactII.class.getResource("Data/zaf_pop.grd");

        Bag empty = new Bag();
        try {
            ShapeFileImporter.read(magisterialBoundaries, magisterialDistricts);//, empty, CountingGeomWrapper.class);
            ShapeFileImporter.read(populationBoundaries, spots);//, empty, CountingGeomWrapper.class);
            //InputStream inputStream = new FileInputStream("C:\\Users\\visiting_researcher\\Desktop\\SimpactII\\mason\\SimpactII\\Data\\zaf_pop.asc");
            //GZIPInputStream compressedInputStream = new GZIPInputStream(inputStream);
            //ArcInfoASCGridImporter.read(compressedInputStream, GridDataType.DOUBLE, populations);
        } catch (Exception ex) {
            System.out.println(ex);
            Logger.getLogger(GeoMasonTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // we use either the ConvexHull or Union to determine if the agents are within
        // Fairfax county or not
        //vectorLayer.computeConvexHull();
        //vectorLayer.computeUnion();

    }

    private void addAgents() {
        Bag allRegions = spots.getGeometries();
        
        //System.out.println("numRegions = " + allRegions.numObjs);
        for (int i = 0; i < allRegions.numObjs; i++) {
            MasonGeometry spot = (MasonGeometry) allRegions.get(i);
            
            //get population -- only continue for larger populations
            AttributeValue av =  (AttributeValue) spot.getAttribute("POP07");
            double population = (double) av.getValue() ;
            if (population < 100000)
                continue;
            NUM_AGENTS++;
            // make new agents, put them in the middle of the spot
            Agent a = new Agent(spot.getGeometry().getCentroid());
            
            agents.addGeometry(new MasonGeometry(a.getGeometry()));
            schedule.scheduleRepeating(a);
        }
        schedule.scheduleRepeating(new Steppable() {
            public void step(SimState state) {
                System.out.println("========================");
            }
        }, 1, 1);
        System.out.println("num agents = " + NUM_AGENTS);
    }

    @Override
    public void start() {
        super.start();

        agents.clear(); // remove any agents from previous runs

        // add agents to the simulation
        addAgents();

        // ensure both GeomFields Color same area
        agents.setMBR(magisterialDistricts.getMBR());
    }

    public static void main(String[] args) {
        doLoop(GeoMasonTest.class, args);
        System.exit(0);
    }
}
