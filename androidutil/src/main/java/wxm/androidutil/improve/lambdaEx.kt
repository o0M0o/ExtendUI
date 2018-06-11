@file:Suppress("unused")
package wxm.androidutil.improve

/**
 * @author      WangXM
 * @version     create：2018/5/24
 */

/**
 * similar to 'let' but without return
 */
fun<T> T.let1(term:(T)->Unit){
    term(this)
}