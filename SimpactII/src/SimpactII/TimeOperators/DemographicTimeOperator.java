package SimpactII.TimeOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * 
 * 
 */
public class DemographicTimeOperator extends TimeOperator{
    
    private double[][] maleMortality;
    private double[][] femaleMortality;
    private double[][] femaleChildMortality;
    private double[][] maleChildMortality;
    private int maxYears;
    
    private double[][] fertility; 
    private SimpactII state; //need to grab the RNG and schedule time in remove method
                
    //create anonymous agent to inject 100 new agents every 5 years
    @Override
    public void preProcess(SimpactII state){
        loadMortality();
        loadFertility(); 
        maxYears = fertility[0].length-1;
        this.state = state; 
        
        //go through females and flip a coin whether they have a child
        state.schedule.scheduleRepeating(new Steppable(){
            @Override
            public void step(SimState state) {
                SimpactII s = (SimpactII) state;
                int year = (int) Math.min(maxYears,Math.floor((state.schedule.getTime()+1)/52));
                Bag myAgents = s.network.allNodes;
                int numAgents = myAgents.size();
                for (int i = 0; i < numAgents; i++){
                    Agent agent = (Agent) myAgents.get(i);
                    if(agent.isMale() || agent.age >= 50 || agent.age < 15){ continue; }
                    
                    int age = (int) Math.floor(agent.age/5)-3 ;                    
                    if(s.random.nextDouble() < fertility[age][year]){
                        Agent a = new Agent(s,new HashMap<String,Object>());
                        a.age = 0;                        
                    }
                }
            }
        }, 52);
    }
    
    //no one is replacing anyone here...
    @Override
    public void replace(SimpactII state, Agent agent){ return; }
    
    //remove based on mortality curves -- from ASSA2008 / Johnson paper
    @Override
    public boolean remove(Agent agent){        
        //return false;
        int year = (int) Math.floor(state.schedule.getTime()/52);
        year = (int) Math.min(maxYears, year); //no data beyond maxYears (2025)
        int age = (int) Math.floor(agent.age/5) - 2; 
        age = (int) Math.min(age, 15); //lump > 75 together
        
        //use correct mortality table
        if( agent.isMale())
            if(age < 0)
                return state.random.nextDouble() < (maleChildMortality[(int) Math.floor(agent.age)][year]/52);
            else
                return state.random.nextDouble() < (maleMortality[age][year]/52); //divide by 52 b/c it's yearly mortality            
        else
            if(age < 0)
                return state.random.nextDouble() < (femaleChildMortality[(int) Math.floor(agent.age)][year]/52); 
            else
                return state.random.nextDouble() < (femaleMortality[age][year]/52);             
        //end if statements
            
        
    }

    private void loadMortality() {
        //age: 10-14,15-19,20-24,...,80-84,85+
        //year: 1985,1986,...,2025
        //mortTable[age][year]
        maleMortality = new double[][]{
            {0.000936673, 0.000949614, 0.000948507, 0.00095608, 0.000924816, 0.000964946, 0.000964779, 0.000957921, 0.000954052, 0.000952816, 0.000950416, 0.000955955, 0.000913172, 0.000939103, 0.000938502, 0.000959294, 0.000931169, 0.000922356, 0.000913169, 0.000903613, 0.000893728, 0.000883526, 0.000870419, 0.00085996, 0.000852561, 0.0008479, 0.000844332, 0.000835389, 0.000825943, 0.000816511, 0.000807604, 0.000799849, 0.000792719, 0.00078555, 0.000778345, 0.000770866, 0.000763246, 0.000755734, 0.000748306, 0.000740942, 0.00073365 },
            {0.002583314, 0.002568548, 0.002563564, 0.002565772, 0.002474882, 0.002575768, 0.002578582, 0.002562097, 0.002545196, 0.002535835, 0.002517593, 0.002496664, 0.002142192, 0.002331855, 0.002377128, 0.002380681, 0.002369131, 0.002354864, 0.002337814, 0.002318297, 0.002297954, 0.002278607, 0.002259361, 0.002239435, 0.002218862, 0.002197717, 0.002176036, 0.002148676, 0.00212731, 0.002111288, 0.002099409, 0.00209068, 0.002071371, 0.002050937, 0.002030586, 0.002011103, 0.001993821, 0.001977916, 0.001961923, 0.001945878, 0.001929312 },
            {0.00446671, 0.00449335, 0.004532219, 0.004578289, 0.004466963, 0.004670261, 0.004714575, 0.004721283, 0.004743144, 0.004785546, 0.004830836, 0.004879058, 0.00490766, 0.005010985, 0.004810706, 0.004690148, 0.004643565, 0.004604654, 0.004569011, 0.004537325, 0.004508975, 0.004482369, 0.00445452, 0.004424618, 0.004393301, 0.004361789, 0.004331051, 0.004300383, 0.00426927, 0.004237773, 0.004205936, 0.004173748, 0.004136101, 0.004105743, 0.004079757, 0.004056265, 0.004033507, 0.004003582, 0.003972773, 0.003942447, 0.003913094 },
            {0.00541419, 0.005487102, 0.005575895, 0.005676544, 0.005599299, 0.005893762, 0.006006566, 0.006076933, 0.006155519, 0.006230716, 0.006310913, 0.006399839, 0.006629477, 0.006837118, 0.006416735, 0.005787935, 0.005605185, 0.005564798, 0.005524192, 0.005483138, 0.005441706, 0.005400293, 0.005359432, 0.005319563, 0.005280809, 0.005243001, 0.005205777, 0.005168509, 0.00513108, 0.005093611, 0.005056316, 0.005019418, 0.004982784, 0.004946312, 0.004910019, 0.00487391, 0.00483799, 0.004801294, 0.00476644, 0.004732299, 0.004698605 },
            {0.005848864, 0.005949424, 0.006062239, 0.006182475, 0.00612582, 0.006430871, 0.006562931, 0.006667991, 0.006790234, 0.006912283, 0.007036518, 0.00715831, 0.007341672, 0.007666404, 0.007279769, 0.006305369, 0.006010174, 0.005951451, 0.005894052, 0.00583825, 0.005783883, 0.005730564, 0.00567775, 0.005624956, 0.005571914, 0.005518697, 0.005465626, 0.005413256, 0.005362035, 0.005312131, 0.005263364, 0.005215431, 0.005167853, 0.005120338, 0.005072837, 0.005025619, 0.004979027, 0.00493293, 0.004887219, 0.004841893, 0.004796973 },
            {0.006742747, 0.006847282, 0.006962078, 0.007082852, 0.007030508, 0.007324245, 0.007445803, 0.007537471, 0.007640415, 0.007742654, 0.007847435, 0.007954996, 0.008466551, 0.008600844, 0.008310924, 0.007209749, 0.007046733, 0.006972145, 0.006897491, 0.006821875, 0.006744954, 0.006667375, 0.006590768, 0.006516863, 0.006446432, 0.006379176, 0.006314019, 0.006249514, 0.006184236, 0.006117352, 0.00604897, 0.005980198, 0.005912825, 0.005848368, 0.005787399, 0.005729432, 0.005673394, 0.005617196, 0.005560047, 0.005502182, 0.005444523 },
            {0.00874723, 0.008806214, 0.008896824, 0.00901823, 0.008980265, 0.009320763, 0.009483282, 0.009617934, 0.00976248, 0.009906878, 0.010048853, 0.010189157, 0.010704057, 0.01077921, 0.010307959, 0.00904874, 0.009015151, 0.008919578, 0.008824091, 0.008729359, 0.0086363, 0.008545137, 0.00845527, 0.008365099, 0.008273175, 0.008178892, 0.008083251, 0.007988849, 0.007898485, 0.007813445, 0.007733286, 0.007656261, 0.007580006, 0.007502101, 0.007421114, 0.007337184, 0.007252166, 0.00716911, 0.007090648, 0.007017821, 0.006949869 },
            {0.012318188, 0.012445847, 0.012566099, 0.01265901, 0.012513321, 0.012774595, 0.01284008, 0.012912215, 0.013037653, 0.013198934, 0.013374659, 0.013553049, 0.01422209, 0.014519613, 0.014180566, 0.012826914, 0.012377197, 0.012252932, 0.012132734, 0.012016909, 0.011904581, 0.011794333, 0.011683962, 0.011572788, 0.01146211, 0.011353706, 0.011248082, 0.011144153, 0.011038987, 0.010929922, 0.010815803, 0.010698353, 0.010582311, 0.010472808, 0.010372235, 0.010279835, 0.010192465, 0.010105794, 0.010015291, 0.009918283, 0.009814979 },
            {0.018047298, 0.018118467, 0.018208526, 0.018312183, 0.018177622, 0.018537797, 0.018639322, 0.018674787, 0.018692932, 0.018709272, 0.018707887, 0.018724077, 0.019368365, 0.019856053, 0.019803469, 0.018631111, 0.017915269, 0.017772628, 0.017627782, 0.017479752, 0.017329632, 0.017179748, 0.01703265, 0.016889992, 0.016752191, 0.016618351, 0.016486867, 0.016354944, 0.016221861, 0.016089533, 0.015960192, 0.015834132, 0.015709896, 0.015583595, 0.015451627, 0.015312517, 0.015168938, 0.015027212, 0.014894021, 0.014772369, 0.014661193 },
            {0.024742221, 0.024873868, 0.024998615, 0.025097817, 0.02491364, 0.02521476, 0.025259481, 0.02526511, 0.025300588, 0.025386298, 0.025463556, 0.025530796, 0.026432004, 0.026580352, 0.026489965, 0.025375045, 0.02441627, 0.024191071, 0.023986244, 0.023801849, 0.02363069, 0.023464397, 0.023297415, 0.02312722, 0.022952507, 0.022774682, 0.022596704, 0.022421786, 0.022252101, 0.022088352, 0.021929564, 0.021773847, 0.021617893, 0.021460664, 0.021304152, 0.021150964, 0.021001599, 0.020854345, 0.020704702, 0.020548566, 0.020384158 },
            {0.034256524, 0.034105431, 0.034007024, 0.033965573, 0.033727098, 0.033977382, 0.033979609, 0.033917137, 0.03383967, 0.03378253, 0.033688486, 0.033585815, 0.034533428, 0.03481406, 0.035623057, 0.034701474, 0.033799081, 0.033600696, 0.033376285, 0.033116333, 0.032825775, 0.0325234, 0.032235283, 0.031978733, 0.031753927, 0.031549253, 0.03135123, 0.031150852, 0.030944238, 0.030729401, 0.030508798, 0.030287357, 0.030070435, 0.029861628, 0.029662127, 0.029470315, 0.029283059, 0.029094953, 0.028904248, 0.028714023, 0.028528343 },
            {0.048230163, 0.048412513, 0.048606652, 0.0487518, 0.048576081, 0.048708653, 0.048618436, 0.048543549, 0.048572225, 0.048734817, 0.048928472, 0.049125311, 0.049740201, 0.050031147, 0.050756846, 0.050059041, 0.049404415, 0.049018376, 0.048661635, 0.048352076, 0.048090524, 0.047860609, 0.047623776, 0.047346758, 0.047013492, 0.046630404, 0.046225907, 0.045840606, 0.045502275, 0.045213006, 0.044955618, 0.044708848, 0.044457845, 0.044196626, 0.043921411, 0.04363603, 0.043348463, 0.043067846, 0.042800341, 0.042548583, 0.042310046 },
            {0.066435031, 0.067091842, 0.067571433, 0.068104889, 0.068535136, 0.069173816, 0.069677149, 0.070149267, 0.070551502, 0.070811807, 0.070951132, 0.071089648, 0.070739635, 0.070725054, 0.070885495, 0.070900482, 0.070905432, 0.070859229, 0.070766768, 0.070618847, 0.070427621, 0.070221534, 0.070020754, 0.069855298, 0.069747654, 0.069698452, 0.069684584, 0.069655846, 0.069569537, 0.069406282, 0.069174983, 0.068913568, 0.06867464, 0.068493207, 0.068369267, 0.068280392, 0.068200125, 0.068110841, 0.068004865, 0.06787882, 0.06773808 },
            {0.090188679, 0.090934966, 0.091728117, 0.092936096, 0.094346923, 0.095461673, 0.0964787, 0.09729102, 0.098144588, 0.09894767, 0.099739024, 0.100439279, 0.099945733, 0.099754025, 0.099321882, 0.098869309, 0.098448882, 0.098101902, 0.097891324, 0.097806768, 0.097796541, 0.097802728, 0.097779867, 0.097699029, 0.097548935, 0.097344875, 0.097123229, 0.096908812, 0.096740076, 0.096645753, 0.096626332, 0.096650783, 0.096654908, 0.096583895, 0.096412974, 0.096153487, 0.095854973, 0.095585377, 0.095389315, 0.09526577, 0.095186297 },
            {0.117916771, 0.119312923, 0.120701798, 0.122097827, 0.123912588, 0.125054167, 0.126623995, 0.128283193, 0.130429895, 0.132582891, 0.134623231, 0.136313488, 0.135898708, 0.135930279, 0.135853735, 0.135710006, 0.135414406, 0.135511237, 0.135516448, 0.135278085, 0.134830081, 0.134316598, 0.133897604, 0.133672554, 0.133627264, 0.133688991, 0.133775169, 0.133820278, 0.133781936, 0.13364334, 0.133426389, 0.133183946, 0.132952108, 0.132786881, 0.132730213, 0.132783286, 0.132901314, 0.132990302, 0.132968948, 0.132799814, 0.132499146 },
            {0.168310696, 0.174582082, 0.185501633, 0.174385074, 0.183907338, 0.172432461, 0.179601621, 0.182743093, 0.185078072, 0.187057167, 0.188961813, 0.191099921, 0.191721706, 0.193945826, 0.19642756, 0.198826154, 0.200887506, 0.202664402, 0.20408736, 0.205206723, 0.206276578, 0.207352208, 0.208343073, 0.209097506, 0.209417223, 0.209172979, 0.208492167, 0.20763768, 0.206895154, 0.206428986, 0.206293949, 0.206409972, 0.206651868, 0.206870358, 0.206956223, 0.206854283, 0.206610192, 0.20626527, 0.205958377, 0.205831565, 0.205964069 },
        };
        
        femaleMortality = new double[][]{
            {0.000507658, 0.00051279, 0.000502744, 0.000504797, 0.000487443, 0.000506045, 0.000512063, 0.00051566, 0.000520937, 0.000535783, 0.000545536, 0.000559865, 0.000574617, 0.000600581, 0.000586308, 0.000561388, 0.0005371, 0.000527398, 0.000517704, 0.000508064, 0.000498486, 0.000488946, 0.000477865, 0.000468161, 0.000459872, 0.000452819, 0.000447813, 0.000439467, 0.000431022, 0.000422684, 0.000414613, 0.000407216, 0.000400271, 0.000393382, 0.000386604, 0.000379798, 0.000372999, 0.000366344, 0.000359818, 0.000353413, 0.000347129},
            {0.001114494, 0.001124942, 0.001138705, 0.001154851, 0.00112658, 0.001189749, 0.001205415, 0.001213555, 0.00122186, 0.001236444, 0.001246559, 0.001255839, 0.001316202, 0.001540118, 0.001696005, 0.001640289, 0.001594632, 0.001568786, 0.001541902, 0.001514376, 0.001486949, 0.001460388, 0.001434207, 0.001408251, 0.001382607, 0.001357278, 0.001332199, 0.001304008, 0.00127977, 0.001258532, 0.001239187, 0.001220615, 0.001198405, 0.001176063, 0.001154235, 0.001133204, 0.001113314, 0.001094206, 0.001075353, 0.001056722, 0.001038197},
            {0.001852005, 0.001873795, 0.00189882, 0.001924568, 0.001878155, 0.001968551, 0.001984342, 0.001990999, 0.00200327, 0.002029935, 0.002054421, 0.002079055, 0.002321635, 0.002488655, 0.002531871, 0.002236934, 0.002063862, 0.002026145, 0.001989325, 0.0019535, 0.001918635, 0.001884644, 0.001851275, 0.001818418, 0.001785967, 0.001753967, 0.001722669, 0.001691967, 0.001661808, 0.001632193, 0.001603117, 0.001574569, 0.00154623, 0.001518695, 0.001491719, 0.001465634, 0.001440629, 0.00141522, 0.001390235, 0.001365691, 0.001341609},
            {0.002242601, 0.002274844, 0.002312705, 0.002354219, 0.002318932, 0.002436, 0.002470467, 0.002492842, 0.002516377, 0.002546458, 0.002574329, 0.002604307, 0.002930766, 0.00301016, 0.002970326, 0.002440722, 0.002343525, 0.00230142, 0.002259775, 0.00221845, 0.002177487, 0.002137109, 0.002097622, 0.002059284, 0.002022181, 0.001986214, 0.001951207, 0.001916683, 0.001882522, 0.001848755, 0.001815516, 0.001783011, 0.001751109, 0.00171976, 0.001688975, 0.001658744, 0.001629047, 0.001599196, 0.001570587, 0.001542999, 0.001516297},
            {0.00257794, 0.002623681, 0.002674625, 0.002728593, 0.002704196, 0.002835098, 0.002885658, 0.002927337, 0.002974327, 0.003030259, 0.003084064, 0.003136777, 0.003455315, 0.003551562, 0.003492875, 0.002836837, 0.002696084, 0.002644134, 0.002593895, 0.002545544, 0.002498855, 0.002453387, 0.002408648, 0.002364232, 0.00231992, 0.002275798, 0.002232237, 0.00218974, 0.002148748, 0.002109408, 0.002071554, 0.00203492, 0.001998747, 0.001962838, 0.00192722, 0.001892074, 0.001857831, 0.001824255, 0.001791265, 0.001758878, 0.001727088},
            {0.003268226, 0.003318339, 0.00337208, 0.003427286, 0.003398686, 0.003532634, 0.003581888, 0.003620706, 0.003663179, 0.003713462, 0.003761771, 0.003810934, 0.004171772, 0.004222945, 0.004244686, 0.00353729, 0.003480452, 0.003418835, 0.003357504, 0.003295792, 0.003233594, 0.003171524, 0.003110807, 0.003052577, 0.002997246, 0.002944443, 0.002893336, 0.002842924, 0.002792349, 0.002741132, 0.002689428, 0.00263803, 0.002588034, 0.00254048, 0.002495744, 0.002453518, 0.002413266, 0.002373197, 0.002332851, 0.002292325, 0.002252133},
            {0.004702861, 0.004699417, 0.004713985, 0.004747247, 0.004699891, 0.004844291, 0.004892747, 0.004926227, 0.004961705, 0.005006104, 0.005046356, 0.005086329, 0.005504937, 0.005659378, 0.005654761, 0.004915033, 0.004905578, 0.004822175, 0.004740527, 0.004661281, 0.004584837, 0.004510848, 0.004438222, 0.004365444, 0.004291533, 0.004216369, 0.004140966, 0.004067271, 0.003997094, 0.003931088, 0.003868674, 0.003808509, 0.003748947, 0.003688612, 0.003626761, 0.003563657, 0.003500603, 0.003439343, 0.003381599, 0.003327966, 0.003277952},
            {0.007091368, 0.007080863, 0.007066206, 0.007035209, 0.006869241, 0.00692405, 0.006866988, 0.006813571, 0.00678619, 0.006786044, 0.006788414, 0.006790019, 0.007331559, 0.007685013, 0.007925475, 0.007274928, 0.006910597, 0.006799771, 0.006691584, 0.006585845, 0.006482258, 0.006380597, 0.006280483, 0.006182134, 0.006086284, 0.005993477, 0.005903512, 0.005815261, 0.005727033, 0.005637695, 0.005547075, 0.005456247, 0.005367385, 0.00528257, 0.005202592, 0.005126801, 0.005053679, 0.004981298, 0.004908062, 0.004833088, 0.004756673},
            {0.010561862, 0.010486577, 0.010419046, 0.010356726, 0.010150064, 0.010231493, 0.010160188, 0.010056205, 0.009942311, 0.009837622, 0.009715498, 0.009597448, 0.01004074, 0.010341469, 0.010827749, 0.010396946, 0.009912033, 0.009758878, 0.009607854, 0.009458952, 0.009312869, 0.009170238, 0.009031238, 0.008895493, 0.008762567, 0.008631975, 0.008503437, 0.008376429, 0.008251406, 0.008129659, 0.008012144, 0.007898538, 0.007787152, 0.007675452, 0.007561721, 0.007445681, 0.007329014, 0.00721499, 0.007106695, 0.007005255, 0.00690969},
            {0.014827561, 0.014891581, 0.014954101, 0.01500332, 0.01486234, 0.015026482, 0.015009573, 0.014963137, 0.014928389, 0.014938344, 0.014945682, 0.01495256, 0.015166733, 0.015138446, 0.015245454, 0.01487073, 0.014685035, 0.014430006, 0.014196507, 0.013983791, 0.013784387, 0.013590967, 0.013400065, 0.013211406, 0.013024983, 0.012841893, 0.012663093, 0.012488782, 0.012318347, 0.012151048, 0.011986079, 0.011823062, 0.011661293, 0.011501689, 0.011346624, 0.011197904, 0.011054888, 0.010914638, 0.010772901, 0.010626739, 0.010475527},
            {0.021033424, 0.021022874, 0.021057159, 0.021146788, 0.021114855, 0.021425337, 0.021568553, 0.021669861, 0.021753262, 0.021853436, 0.021919069, 0.021971211, 0.021712016, 0.021602432, 0.021982524, 0.021450468, 0.020983617, 0.02073532, 0.020472249, 0.020184972, 0.019874635, 0.019553521, 0.019244397, 0.018962, 0.018705851, 0.018466576, 0.018234639, 0.018005395, 0.017778622, 0.017554163, 0.01733328, 0.017117123, 0.016905902, 0.016698794, 0.016494935, 0.016293437, 0.016093944, 0.015895763, 0.015700176, 0.015510115, 0.015327717},
            {0.030129568, 0.030198997, 0.030283085, 0.030352074, 0.030207349, 0.030272741, 0.030210225, 0.03017809, 0.030222537, 0.030360403, 0.030515811, 0.030671926, 0.030536966, 0.030571723, 0.030985307, 0.030619024, 0.030299635, 0.029861378, 0.029447917, 0.02906945, 0.02872582, 0.028403838, 0.02808643, 0.027750651, 0.027382673, 0.026982944, 0.026566493, 0.026164055, 0.025796672, 0.025465811, 0.025159458, 0.02486393, 0.024571991, 0.024283535, 0.023997815, 0.023716462, 0.023441319, 0.023172893, 0.022909916, 0.022651267, 0.022395692},
            {0.042467809, 0.042820647, 0.043030256, 0.043199345, 0.043236755, 0.043350567, 0.043351967, 0.043366917, 0.043369671, 0.0432833, 0.043116311, 0.042944031, 0.042897976, 0.042955454, 0.043135031, 0.043246791, 0.043359077, 0.043106995, 0.042823269, 0.042499593, 0.042144088, 0.041775092, 0.041416274, 0.041085911, 0.040799922, 0.040559013, 0.040344501, 0.040131317, 0.039884526, 0.039582235, 0.039224423, 0.038833342, 0.038456256, 0.03812616, 0.037846325, 0.037598871, 0.037362697, 0.037126414, 0.036889992, 0.036652281, 0.036415915},
            {0.060263227, 0.060514167, 0.060724675, 0.061166351, 0.061742733, 0.062121443, 0.062440036, 0.062569691, 0.062637095, 0.062611185, 0.062533513, 0.06239605, 0.062529235, 0.062604447, 0.062470916, 0.062235706, 0.061943167, 0.061606303, 0.061388697, 0.061284946, 0.061249509, 0.061231863, 0.061190755, 0.061099858, 0.060947707, 0.060746129, 0.060521682, 0.060308012, 0.060131095, 0.060013105, 0.05995489, 0.059928961, 0.059898699, 0.059814908, 0.059648087, 0.059398656, 0.059101249, 0.058823513, 0.058611271, 0.058465168, 0.058358459},
            {0.085338823, 0.085919896, 0.086340027, 0.086645278, 0.087105468, 0.087205169, 0.087559163, 0.087862393, 0.088489888, 0.089161855, 0.089723955, 0.090034126, 0.090394441, 0.090588239, 0.090639811, 0.0906222, 0.090522713, 0.090610034, 0.090622577, 0.090407377, 0.089977561, 0.089465309, 0.089036625, 0.088801586, 0.088751168, 0.088811795, 0.088901287, 0.088952143, 0.088920915, 0.088788622, 0.088574629, 0.088322804, 0.088088513, 0.087914385, 0.087836999, 0.08785772, 0.087930826, 0.087996059, 0.087971831, 0.087809201, 0.087509267},
            {0.134964121, 0.138213468, 0.139521604, 0.139668043, 0.140050525, 0.140034644, 0.140152534, 0.140027987, 0.139790937, 0.139474976, 0.139210869, 0.138844436, 0.139201958, 0.140335315, 0.141628157, 0.142732774, 0.143631832, 0.145088988, 0.146292273, 0.147261907, 0.148200121, 0.149148385, 0.150043384, 0.150781954, 0.151195755, 0.151153412, 0.150723993, 0.150099679, 0.14951046, 0.149109501, 0.148970215, 0.149051967, 0.149260306, 0.149474568, 0.149599587, 0.149574011, 0.149403972, 0.149145232, 0.148885397, 0.148734265, 0.14876811},
        };
        
        maleChildMortality = new double[][]{
            {0.024766249, 0.02417905, 0.023639247, 0.0231273, 0.021398847, 0.022003547, 0.021419746, 0.020643374, 0.019980836, 0.019644518, 0.019216943, 0.018738297, 0.018391671, 0.017915948, 0.017464086, 0.017014977, 0.016567663, 0.016122864, 0.015691495, 0.015273138, 0.014867389, 0.01447386, 0.014092171, 0.013721957, 0.013362865, 0.013014553, 0.012676689, 0.012348953, 0.012031034, 0.011722631, 0.011423455, 0.011133222, 0.010851661, 0.010578507, 0.010313504, 0.010056405, 0.009806969, 0.009564964, 0.009330166, 0.009102354, 0.00888132},
            {0.009717881, 0.009494447, 0.009256104, 0.009022475, 0.008322863, 0.008513048, 0.008239722, 0.007910887, 0.007613953, 0.007435591, 0.00724635, 0.007042032, 0.006854752, 0.00667749, 0.006460852, 0.006238038, 0.006016951, 0.005855636, 0.005699299, 0.005547787, 0.005400948, 0.005258639, 0.00512072, 0.004987055, 0.004857512, 0.004731964, 0.004610287, 0.004492362, 0.004378072, 0.004267305, 0.004159953, 0.00405591, 0.003955073, 0.003857344, 0.003762627, 0.003670828, 0.003581857, 0.003495628, 0.003412056, 0.003331058, 0.003252555},
            {0.003680923, 0.00359419, 0.003524202, 0.003441912, 0.003180707, 0.003257001, 0.003153698, 0.003026676, 0.002916158, 0.002846293, 0.002771842, 0.002703432, 0.002632308, 0.002565465, 0.002496158, 0.002417468, 0.002338019, 0.002269752, 0.002203548, 0.002139344, 0.002077079, 0.002016695, 0.001958136, 0.001901345, 0.00184627, 0.001792859, 0.00174106, 0.001690827, 0.00164211, 0.001594865, 0.001549046, 0.001504612, 0.001461519, 0.001419728, 0.001379198, 0.001339893, 0.001301774, 0.001264807, 0.001228956, 0.001194187, 0.001160468},
            {0.001829555, 0.001779739, 0.001742411, 0.001707955, 0.001582312, 0.001618247, 0.001567999, 0.001504798, 0.001449091, 0.001415371, 0.001376989, 0.001341799, 0.001309165, 0.001274961, 0.001242688, 0.001209551, 0.001172936, 0.001139378, 0.001106843, 0.001075298, 0.001044714, 0.001015062, 0.000986313, 0.00095844, 0.000931416, 0.000905214, 0.000879811, 0.000855182, 0.000831302, 0.00080815, 0.000785703, 0.00076394, 0.00074284, 0.000722382, 0.000702547, 0.000683317, 0.000664672, 0.000646595, 0.000629068, 0.000612075, 0.0005956},
            {0.001354458, 0.001315888, 0.00128534, 0.001260233, 0.001174313, 0.001203777, 0.001168655, 0.001124427, 0.001084445, 0.001059923, 0.001033187, 0.001006979, 0.000982884, 0.000959814, 0.000936603, 0.000914076, 0.000890257, 0.000869034, 0.000848374, 0.00082826, 0.000808678, 0.000789613, 0.000771052, 0.000752981, 0.000735386, 0.000718255, 0.000701574, 0.000685333, 0.000669518, 0.000654119, 0.000639124, 0.000624522, 0.000610302, 0.000596455, 0.000582971, 0.000569839, 0.00055705, 0.000544595, 0.000532464, 0.00052065, 0.000509144},
            {0.00126319, 0.001230537, 0.001203618, 0.001182006, 0.001104958, 0.001140727, 0.001114008, 0.001076686, 0.001042607, 0.001021667, 0.000997868, 0.000975575, 0.000954221, 0.000934395, 0.000915734, 0.000896356, 0.000878046, 0.000860937, 0.000844186, 0.000827785, 0.000811727, 0.000796004, 0.00078061, 0.000765537, 0.000750779, 0.00073633, 0.000722182, 0.00070833, 0.000694768, 0.000681489, 0.000668487, 0.000655757, 0.000643293, 0.00063109, 0.000619141, 0.000607442, 0.000595987, 0.000584772, 0.000573791, 0.00056304, 0.000552513},
            {0.001139331, 0.001114619, 0.001092929, 0.0010743, 0.001005687, 0.001040268, 0.00102141, 0.000992449, 0.000964973, 0.000949234, 0.000929628, 0.000910764, 0.000893349, 0.000876513, 0.000860811, 0.000844815, 0.000828729, 0.000813401, 0.000798376, 0.000783649, 0.000769214, 0.000755064, 0.000741194, 0.000727598, 0.000714272, 0.000701209, 0.000688405, 0.000675854, 0.000663551, 0.000651492, 0.000639672, 0.000628085, 0.000616727, 0.000605595, 0.000594682, 0.000583985, 0.0005735, 0.000563223, 0.000553148, 0.000543273, 0.000533593},
            {0.00097575, 0.000959517, 0.00094485, 0.000931091, 0.000873299, 0.000903133, 0.000889036, 0.000868614, 0.000848898, 0.00083829, 0.000824349, 0.000809814, 0.000795894, 0.000782992, 0.000770644, 0.000758668, 0.000746501, 0.00073335, 0.000720447, 0.000707788, 0.000695369, 0.000683184, 0.00067123, 0.000659502, 0.000647996, 0.000636707, 0.000625632, 0.000614766, 0.000604106, 0.000593647, 0.000583386, 0.000573319, 0.000563443, 0.000553753, 0.000544246, 0.000534919, 0.000525769, 0.000516791, 0.000507984, 0.000499342, 0.000490865},
            {0.000775292, 0.000766673, 0.000758924, 0.000751041, 0.000706484, 0.000731536, 0.00072099, 0.000706589, 0.000694117, 0.000688434, 0.000679482, 0.000670109, 0.000660102, 0.000650629, 0.000641936, 0.000633165, 0.000624578, 0.000613769, 0.000603165, 0.000592762, 0.000582555, 0.000572541, 0.000562717, 0.000553078, 0.000543622, 0.000534344, 0.000525242, 0.000516312, 0.000507551, 0.000498956, 0.000490523, 0.00048225, 0.000474133, 0.000466169, 0.000458356, 0.000450691, 0.000443171, 0.000435793, 0.000428555, 0.000421453, 0.000414486},
            {0.000620508, 0.000617672, 0.000615485, 0.000612801, 0.000579338, 0.000601832, 0.000594251, 0.00058322, 0.000574528, 0.000572566, 0.000567959, 0.000562417, 0.000556391, 0.000549936, 0.000543711, 0.000537524, 0.00053135, 0.000524063, 0.000516894, 0.00050984, 0.000502901, 0.000496074, 0.000489356, 0.000482747, 0.000476245, 0.000469847, 0.000463552, 0.000457359, 0.000451264, 0.000445268, 0.000439368, 0.000433562, 0.000427849, 0.000422227, 0.000416695, 0.000411252, 0.000405895, 0.000400624, 0.000395437, 0.000390332, 0.000385308},
        };
                
        femaleChildMortality = new double[][]{
            {0.022240816, 0.021789276, 0.021271179, 0.020793268, 0.019227941, 0.019748092, 0.019194452, 0.018494391, 0.017851482, 0.017498032, 0.017101044, 0.016649286, 0.016376772, 0.015965403, 0.015557177, 0.015151564, 0.014747681, 0.014340619, 0.013946117, 0.013563779, 0.013193219, 0.012834068, 0.012485965, 0.012148565, 0.01182153, 0.011504536, 0.011197269, 0.010899424, 0.010610706, 0.01033083, 0.010059521, 0.009796513, 0.009541545, 0.009294369, 0.009054742, 0.00882243, 0.008597205, 0.008378849, 0.008167149, 0.007961898, 0.007762897},
            {0.009006322, 0.008822323, 0.008563082, 0.008324392, 0.007660114, 0.007819104, 0.007545815, 0.007232566, 0.006940368, 0.006758848, 0.006576492, 0.006378186, 0.006211269, 0.006063403, 0.005865664, 0.005659511, 0.005454968, 0.005296697, 0.005143663, 0.004995694, 0.004852621, 0.004714281, 0.004580517, 0.004451179, 0.004326118, 0.004205193, 0.004088267, 0.003975208, 0.003865887, 0.00376018, 0.003657968, 0.003559134, 0.003463568, 0.00337116, 0.003281806, 0.003195405, 0.00311186, 0.003031075, 0.002952959, 0.002877425, 0.002804386},
            {0.002896234, 0.002835334, 0.002770136, 0.00269401, 0.002482534, 0.002535903, 0.002447753, 0.002343998, 0.00225068, 0.002190787, 0.002130942, 0.002074105, 0.002018455, 0.001968083, 0.001917824, 0.00185685, 0.001794387, 0.001737392, 0.001682276, 0.001628977, 0.001577436, 0.001527594, 0.001479396, 0.001432786, 0.001387713, 0.001344126, 0.001301976, 0.001261216, 0.001221799, 0.001183681, 0.00114682, 0.001111173, 0.001076702, 0.001043367, 0.001011131, 0.000979957, 0.00094981, 0.000920657, 0.000892465, 0.000865202, 0.000838838},
            {0.001278534, 0.001247862, 0.001216296, 0.001186836, 0.001094523, 0.001114955, 0.00107603, 0.001029682, 0.000986786, 0.000960088, 0.000932842, 0.000907615, 0.000884212, 0.000860648, 0.000839217, 0.000817659, 0.000793014, 0.000768676, 0.000745141, 0.000722383, 0.000700375, 0.000679093, 0.000658513, 0.000638612, 0.000619367, 0.000600756, 0.00058276, 0.000565357, 0.000548528, 0.000532254, 0.000516517, 0.000501299, 0.000486582, 0.000472351, 0.00045859, 0.000445282, 0.000432413, 0.000419968, 0.000407934, 0.000396296, 0.000385043},
            {0.000846386, 0.000825671, 0.000802336, 0.000781974, 0.000724528, 0.00073726, 0.000711522, 0.000681534, 0.000653265, 0.000635387, 0.000618139, 0.000601382, 0.000586095, 0.000571367, 0.000556862, 0.00054319, 0.000528938, 0.00051362, 0.000498788, 0.000484427, 0.000470522, 0.000457059, 0.000444022, 0.000431399, 0.000419177, 0.000407343, 0.000395884, 0.000384789, 0.000374045, 0.000363642, 0.000353569, 0.000343816, 0.000334372, 0.000325227, 0.000316372, 0.000307798, 0.000299495, 0.000291456, 0.000283671, 0.000276133, 0.000268834},
            {0.000768095, 0.000750815, 0.000730779, 0.000713444, 0.000663522, 0.000678591, 0.000657927, 0.0006326, 0.000608743, 0.00059388, 0.000579027, 0.000565028, 0.000551742, 0.000539222, 0.000527237, 0.000515097, 0.000503801, 0.000489948, 0.000476505, 0.000463459, 0.000450798, 0.000438512, 0.000426589, 0.000415018, 0.000403789, 0.000392891, 0.000382316, 0.000372054, 0.000362094, 0.000352429, 0.00034305, 0.000333948, 0.000325114, 0.000316542, 0.000308223, 0.00030015, 0.000292316, 0.000284713, 0.000277334, 0.000270174, 0.000263225},
            {0.000692637, 0.000678814, 0.000662395, 0.000648147, 0.000604704, 0.000620208, 0.000604489, 0.000584107, 0.000564444, 0.000552728, 0.000540465, 0.000528549, 0.000517496, 0.000506837, 0.000496738, 0.000486439, 0.000476311, 0.000463195, 0.000450461, 0.000438097, 0.000426092, 0.000414436, 0.000403119, 0.000392131, 0.000381462, 0.000371103, 0.000361045, 0.00035128, 0.000341798, 0.000332592, 0.000323653, 0.000314975, 0.000306548, 0.000298366, 0.000290422, 0.000282709, 0.00027522, 0.000267949, 0.000260889, 0.000254034, 0.000247379},
            {0.000593299, 0.000583207, 0.00057113, 0.000560535, 0.000524849, 0.000539323, 0.00052763, 0.000512629, 0.000497919, 0.000489525, 0.000480479, 0.000471262, 0.000462345, 0.000454001, 0.000445966, 0.000438032, 0.000430017, 0.000418244, 0.000406808, 0.000395702, 0.000384914, 0.000374436, 0.000364258, 0.000354373, 0.000344772, 0.000335447, 0.000326389, 0.000317591, 0.000309046, 0.000300747, 0.000292685, 0.000284856, 0.000277251, 0.000269864, 0.00026269, 0.000255721, 0.000248953, 0.000242379, 0.000235993, 0.000229791, 0.000223767},
            {0.000482355, 0.000475667, 0.000467404, 0.000460193, 0.000432448, 0.000445354, 0.00043702, 0.000426342, 0.000416154, 0.000410838, 0.000404795, 0.000398462, 0.000391909, 0.000385637, 0.000379765, 0.000373833, 0.00036793, 0.000358264, 0.000348866, 0.00033973, 0.000330848, 0.000322213, 0.000313818, 0.000305656, 0.000297721, 0.000290008, 0.000282508, 0.000275217, 0.000268129, 0.000261238, 0.000254538, 0.000248025, 0.000241693, 0.000235536, 0.000229551, 0.000223733, 0.000218076, 0.000212576, 0.000207229, 0.000202031, 0.000196977},
            {0.000403466, 0.000399519, 0.00039426, 0.000389504, 0.000367612, 0.000379515, 0.000373634, 0.0003658, 0.000358545, 0.000355367, 0.000351659, 0.000347685, 0.000343216, 0.000338702, 0.000334325, 0.000329977, 0.000325598, 0.000318542, 0.000311654, 0.000304928, 0.000298362, 0.00029195, 0.00028569, 0.000279578, 0.000273611, 0.000267784, 0.000262094, 0.000256538, 0.000251113, 0.000245816, 0.000240643, 0.000235592, 0.000230659, 0.000225842, 0.000221138, 0.000216545, 0.000212059, 0.000207678, 0.000203399, 0.000199221, 0.000195141},
        };
        
    }
    
    private void loadFertility(){
        //age: 10-14,15-19,20-24,...,40-44,45-49 (no children thereafter
        //year: 1985,1986,...,2025
        //mortTable[age][year]
        fertility = new double[][] {
            {0.08217028, 0.081803008, 0.081587681, 0.081630468, 0.081920553, 0.082331365, 0.082785163, 0.083010235, 0.082902081, 0.082410994, 0.081574666, 0.077949144, 0.074244361, 0.070854408, 0.067808287, 0.06507499, 0.064191607, 0.063303667, 0.062363494, 0.061378314, 0.060389904, 0.059459757, 0.058550692, 0.05764763, 0.056755191, 0.055871894, 0.054990722, 0.053799674, 0.052955375, 0.052350118, 0.05191373, 0.051571957, 0.050833967, 0.050055678, 0.049302134, 0.048601726, 0.048000078, 0.047461741, 0.046925989, 0.046390116, 0.045838368},
            {0.191383228, 0.187518885, 0.183556558, 0.179444568, 0.175167634, 0.170737451, 0.165955567, 0.161078121, 0.15612925, 0.151014795, 0.145642416, 0.13988183, 0.13388732, 0.128251319, 0.122991404, 0.118055972, 0.117051627, 0.116085105, 0.11520127, 0.11441599, 0.113716761, 0.113076264, 0.112412388, 0.11170646, 0.110972194, 0.110236569, 0.109528363, 0.10882826, 0.108130349, 0.107438135, 0.106751706, 0.106068248, 0.105240487, 0.104581989, 0.104043336, 0.103577583, 0.103132334, 0.102525497, 0.101903181, 0.101296489, 0.100718466},
            {0.209300521, 0.204623702, 0.199945507, 0.195216926, 0.190358795, 0.185291507, 0.179596437, 0.173612748, 0.167461929, 0.161196994, 0.15480948, 0.151011229, 0.146964458, 0.143220904, 0.139946193, 0.137156407, 0.134570504, 0.132282123, 0.130266685, 0.128504781, 0.126970622, 0.125627648, 0.124430897, 0.1233388, 0.122324839, 0.121379414, 0.120502539, 0.119710265, 0.119006089, 0.118379933, 0.117811101, 0.117282975, 0.116797378, 0.116351303, 0.115939677, 0.11555853, 0.115204831, 0.114890256, 0.114583585, 0.114287322, 0.114001458},
            {0.165603111, 0.162174991, 0.158630422, 0.154982973, 0.151263826, 0.147487606, 0.143422188, 0.139175796, 0.134729538, 0.130049773, 0.125124923, 0.123299193, 0.121329485, 0.119435461, 0.117807268, 0.116579118, 0.112475077, 0.10906081, 0.106175733, 0.103703522, 0.101573731, 0.099751754, 0.098223679, 0.096983561, 0.096018248, 0.095293646, 0.094747314, 0.094300741, 0.093879197, 0.093441164, 0.092982833, 0.092519238, 0.092132619, 0.091850558, 0.09166212, 0.091525638, 0.091386631, 0.091271674, 0.091186904, 0.09112598, 0.091085958},
            {0.110840485, 0.10895962, 0.106997609, 0.10497429, 0.102874584, 0.100681805, 0.098324599, 0.095794127, 0.093113534, 0.090313165, 0.087424225, 0.084973562, 0.082452442, 0.079913821, 0.077359647, 0.074806362, 0.071900852, 0.069236974, 0.066832072, 0.064691628, 0.062800996, 0.061117784, 0.059577383, 0.058119889, 0.056715738, 0.05536723, 0.054098355, 0.052941945, 0.051927579, 0.051069778, 0.050357025, 0.04974664, 0.049180978, 0.048602227, 0.047984677, 0.047334738, 0.046671929, 0.046087143, 0.045599559, 0.045196206, 0.044843975},
            {0.058123549, 0.056700513, 0.055521857, 0.054380123, 0.053178177, 0.051882651, 0.050482506, 0.049001408, 0.047454003, 0.045834512, 0.044140252, 0.041887716, 0.039598019, 0.037293284, 0.034991373, 0.032715121, 0.030969991, 0.029383782, 0.027936541, 0.026602071, 0.025362501, 0.024216693, 0.023173452, 0.02224762, 0.021444575, 0.020755422, 0.020152007, 0.01959691, 0.019058818, 0.018526938, 0.018007948, 0.017520079, 0.017082841, 0.016710621, 0.01640726, 0.016163792, 0.015956503, 0.015759276, 0.015547815, 0.01531626, 0.015071142},
            {0.023287942, 0.021563243, 0.01981084, 0.018140837, 0.016633552, 0.015322161, 0.014150236, 0.012990746, 0.011747746, 0.01040531, 0.008985057, 0.008902336, 0.009005132, 0.009145663, 0.009317542, 0.009510381, 0.008379574, 0.00745972, 0.006708593, 0.006092974, 0.005587021, 0.005169959, 0.004826797, 0.004542447, 0.004301698, 0.00409303, 0.003911779, 0.003758179, 0.003635482, 0.003545638, 0.003486838, 0.003449885, 0.003420493, 0.003386037, 0.003341268, 0.003288241, 0.003234538, 0.003190276, 0.003163873, 0.003159258, 0.003174291},
        };
    }
}
