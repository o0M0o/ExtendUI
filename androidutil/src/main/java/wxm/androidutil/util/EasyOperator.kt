package wxm.androidutil.util


/**
 * @author      WangXM
 * @version     create：2018/5/23
 */

/**
 * if [T] is null run [nullTerm] else run [term]
 */
fun <T, R> T?.forObj(term:(t:T)->R, nullTerm:()->R): R {
    return this.let{
        if(null == it) nullTerm()  else term(it!!)
    }
}


/**
 * if term is true run [trueTerm] else run [falseTerm]
 */
fun <R> (()-> Boolean).doJudge(trueTerm:()->R, falseTerm:()->R): R {
    return if(this())  trueTerm()   else falseTerm()
}