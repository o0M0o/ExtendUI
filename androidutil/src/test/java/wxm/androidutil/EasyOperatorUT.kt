package com.wxm.KeepAccountJavaTest

import junit.framework.Assert
import org.junit.Test
import wxm.androidutil.util.doJudge
import wxm.androidutil.util.forObj


/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
class EasyOperatorUT   {
    @Test
    fun testDoJude()   {
        Assert.assertTrue({1 == 1}.doJudge({true}, {false}))
        Assert.assertFalse({1 == 2}.doJudge({true}, {false}))

        Assert.assertTrue(true.doJudge({true}, {false}))
        Assert.assertFalse(false.doJudge({true}, {false}))

        Assert.assertTrue(true.doJudge(true, false))
        Assert.assertFalse(false.doJudge(true, false))
    }

    @Test
    fun testDoForObj()   {
        Assert.assertEquals("abc", "abc".forObj({t -> t}, {"false"}))
        Assert.assertEquals(1, 1.forObj({t -> t}, {0}))
        Assert.assertEquals(0, null.forObj({t -> t}, {0}))

        Assert.assertEquals("abc", "abc".forObj("abc", "false"))
        Assert.assertEquals(1, 1.forObj(1, 0))
        Assert.assertEquals(0, null.forObj(1, 0))
    }
}