import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.carboncompass.R

class CustomAdapter(val context: Context, val transportList: List<String>, val imageList: List<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return transportList.size
    }

    override fun getItem(position: Int): Any {
        return transportList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        }

        val imageView = view!!.findViewById<ImageView>(R.id.spinner_image)
        val textView = view.findViewById<TextView>(R.id.spinner_text)

        imageView.setImageResource(imageList[position])
        textView.text = transportList[position]

        return view
    }
}
