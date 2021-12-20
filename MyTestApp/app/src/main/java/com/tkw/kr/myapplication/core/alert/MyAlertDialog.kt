package com.tkw.kr.myapplication.core.alert

import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import kotlinx.android.synthetic.main.alert_custom.view.*

class MyAlertDialog(context: Context): AlertDialog(context) {
    companion object {
        fun showAlert(context: Context, message: String) : AlertDialog {
            return showAlert(
                context = context,
                message = message,
                fstBtnTitle = R.string.btn_ok
            )
        }

        fun showAlert(context: Context, message: String, title: String): AlertDialog {
            return showAlert(context, message, title, null, null)
        }

        fun showAlert(context: Context, message: String,
                      fstBtnTitle: Int? = null,
                      fstClickListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { _, _ ->  }): AlertDialog {
            return showAlert(context = context, message = message, title = null, fstBtnTitle = fstBtnTitle, fstClickListener = fstClickListener, sndBtnTitle = null, sndClickListener = null)
        }

        fun showAlert(context: Context, message: String, title: String,
                      fstBtnTitle: Int? = null,
                      fstClickListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { _, _ ->  }) : AlertDialog {
            return showAlert(context = context, message = message, title = title, fstBtnTitle = fstBtnTitle, fstClickListener = fstClickListener, sndBtnTitle = null, sndClickListener = null)
        }

        fun showAlert(context: Context, message: String, title: String?,
                      fstBtnTitle: Int? = null,
                      fstClickListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { p0, p1 ->  },
                      sndBtnTitle: Int? = null,
                      sndClickListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { p0, p1 ->  }) : AlertDialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert_custom, null)
            val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme).apply {
                setView(view)
                setCancelable(true)
                setOnKeyListener { dialogInterface, i, keyEvent ->
                    if(i == KeyEvent.KEYCODE_BACK) {
                        dialogInterface.dismiss()
                        true
                    }
                    false
                }
            }

            val alert = builder.create()
            view.message_tv.text = message

            if(!title.isNullOrEmpty()) {
                view.selected_title.setText(title)
            } else {
                view.title_cl.visibility = View.GONE
            }

            if (fstBtnTitle != null) {
                view.fst_btn.visibility = View.VISIBLE
                view.fst_btn.text = context.getString(fstBtnTitle)
                view.fst_btn.setOnSingleClickListener {
                    alert.dismiss()
                    fstClickListener?.onClick(alert, DialogInterface.BUTTON_NEGATIVE)
                }
            }

            if (sndBtnTitle != null) {
                view.snd_btn.visibility = View.VISIBLE
                view.snd_btn.text = context.getString(sndBtnTitle)
                view.snd_btn.setOnSingleClickListener {
                    alert.dismiss()
                    sndClickListener?.onClick(alert, DialogInterface.BUTTON_POSITIVE)
                }
            }
            return alert.also { it.show() }
        }
    }
}