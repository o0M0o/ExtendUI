package com.wxm.KeepAccountJavaTest

import junit.framework.Assert
import org.junit.Test
import wxm.androidutil.tightUUID.tightUUID
import java.util.*


/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
class TightUUIDUT   {
    @Test
    fun testTightUUID() {
        for(i in 0 until 5000) {
            val org = UUID.randomUUID().toString()
            val trans = tightUUID.toTUUID(org)
            val orgTrans = tightUUID.toUUID(trans)
            //System.out.println("$org : $trans : $orgTrans")
            System.out.println(org)
            System.out.println(trans)
            System.out.println(tightUUID.toTUUID(org, true))
            System.out.println("------------------------")

            Assert.assertEquals(org, orgTrans)
            Assert.assertTrue(org.length > trans.length)
        }
    }
}