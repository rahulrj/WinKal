package wincal.android.com.wincal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
    boolean mLower;
    private View mView;
    private Context mContext;

    public CustomAnimController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAnimController(Animation animation) {
        super(animation);
    }

    public CustomAnimController(Context context,Animation animation, float delay,boolean lower) {
        super(animation, delay);
        mDelay=delay;
        mContext=context;
        mLower=lower;
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

        //int index=getTransformedIndex(animationParameters);


        Log.d("rahul",""+mLower);

        if(!mLower) {

            Log.d("rahul", "yes");
            int index = getTransformedIndex(animationParameters);
            if (index <=mMiddlePosition)
                return (long) (index * mDelay * 200);
            else
                return (long) (index * mDelay * 20000);

        }


        else{

            Log.d("rahul","ye");
            int index = getTransformedIndex(animationParameters);
            if (index < 4)
                return (long) (index * mDelay * 200);
            else
                return (long) (index * mDelay * 20000);

        }

       //return 0;

    }

        @Override
    protected int getTransformedIndex (LayoutAnimationController.AnimationParameters params){

        int index=params.index;
        if(index==mMiddlePosition){

            return 0;
        }

        if(!mLower) {
            for (int i = 1; i < 20; i++) {

                if (index == mMiddlePosition - i) {
                    return i;

                }
            }

            return index;

        }

            else{


            for (int i = 1; i < 20; i++) {

                if (index == mMiddlePosition + i)
                    return i;


            }

                return index+mMiddlePosition+5;

        }





     //   return 0;


    }


//    @Override
//    protected int getTransformedIndex (LayoutAnimationController.AnimationParameters params){
//
//        int index=params.index;
//
//
//        if(index==mMiddlePosition){
//
//            return 0;
//        }
//
//        for(int i=1;i<20;i++){
//
//            if(index==mMiddlePosition-i || index==mMiddlePosition+i){
//                return i;
//
//            }
//        }
//
//        return 0;
//
//
//    }

}
