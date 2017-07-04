package jungle68.com.library.core;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static jungle68.com.library.core.TextPosition.BELOW_SECTION_MARK;
import static jungle68.com.library.core.TextPosition.BOTTOM_SIDES;
import static jungle68.com.library.core.TextPosition.NONE;
import static jungle68.com.library.core.TextPosition.SIDES;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/7/3
 * @Contact master.jungle68@gmail.com
 */
@IntDef({NONE, SIDES, BOTTOM_SIDES, BELOW_SECTION_MARK})
@Retention(RetentionPolicy.SOURCE)
public  @interface TextPosition {
    int NONE = -1, SIDES = 0, BOTTOM_SIDES = 1, BELOW_SECTION_MARK = 2;
}
