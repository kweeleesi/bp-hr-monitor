import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
        a.reset();
        if(v != null){
        v.clearAnimation();
        v.startAnimation(a);
        }
}

 }
