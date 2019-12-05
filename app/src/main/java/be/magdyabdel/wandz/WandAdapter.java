package be.magdyabdel.wandz;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WandAdapter extends RecyclerView.Adapter<WandAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> devices;
    private Context context;

    public WandAdapter(ArrayList<BluetoothDevice> devices, Context context) {
        this.devices = devices;
        this.context = context;
    }


    @Override
    public WandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.wand_row, parent, false);
        WandAdapter.ViewHolder viewHolder = new WandAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WandAdapter.ViewHolder viewHolder, int position) {
        BluetoothDevice bluetoothDevice = devices.get(position);
        TextView textView = viewHolder.deviceName;
        textView.setText(bluetoothDevice.getName());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView deviceName;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.name_wand);
        }
    }
}
