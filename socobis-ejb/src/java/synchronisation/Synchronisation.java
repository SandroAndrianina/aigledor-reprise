package synchronisation;

import synchronisation.utils.ConstanteSyncho;

import java.util.ArrayList;

public class Synchronisation {

    public static void execute() throws Exception {
        String fichier = ConstanteSyncho.TEXTEFILE;
        ArrayList<String> f = Lecture.lireFichier(fichier);
        try{
            if(f.size() > 0)
            {
                if(f.get(0).compareToIgnoreCase("false") == 0) {
                    Lecture.ecrireFichier(fichier,"true");
                    SynchroBase.execute();
                    Lecture.ecrireFichier(fichier,"false");
                }
                else {
                    System.out.println("En cours.");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Lecture.ecrireFichier(fichier,"false");
        }
        finally
        {
            Lecture.ecrireFichier(fichier,"false");
        }
    }

}
