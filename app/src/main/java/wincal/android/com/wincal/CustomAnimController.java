package wincal.android.com.wincal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;

/**
 * Created by Rahul Raja on 2/14/2015.
 */
public class CustomAnimController extends LayoutAnimationController {

    private float mDelay;
    int mMiddlePosition;
    private View mView;
    private Context mContext;

    public CustomAnimController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAnimController(Animation animation) {
        super(animation);
    }

    public CustomAnimController(Context context,Animation animation, float delay) {
        super(animation, delay);
        mDelay=delay;
        mContext=context;
    }

    public void setMiddlePosition(int middlePosition){

        this.mMiddlePosition=middlePosition;
    }

    public int getMiddlePosition(){

        return mMiddlePosition;
    }

    @Override
    protected long getDelayForView (View view){

        ViewGroup.LayoutParams params=view.getLayoutParams();
        LayoutAnimationController.AnimationParameters animationParameters=params.layoutAnimationParameters;

        int index=getTransformedIndex(animationParameters);
        return (long)(index*mDelay*200);

       //return 0;

    }

    @Override
    public void start(){


            return;

    }

    @Override
    protected int getTransformedIndex (LayoutAnimationController.AnimationParameters params){

        int index=params.index;


        if(index==mMiddlePosition){

            return 0;
        }

        for(int i=1;i<20;i++){

            if(index==mMiddlePosition-i || index==mMiddlePosition+i){
                return i;

            }
        }

        return 0;

//        if(index==2)
//            return 0;
//        else if(index==1 || index==3)
//            return 1;
//        else if(index==0 || index==4)
//            return 2;
//        else return 3;
        //return 0;
    }

}
