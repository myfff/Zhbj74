package domain;

import java.util.ArrayList;

/**组图对象
 * Created by yh on 2016/12/20.
 */

public class Photosbean {
    public  PhotosData data;
     public  class  PhotosData{
         public ArrayList<PhotosNews> news;
     }
    public  class  PhotosNews{
        public  int id;
        public  String listimage;
        public  String title;
    }
}
