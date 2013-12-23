package es.deusto.p1justpark.ui;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import es.deusto.p1justpark.data.Parking;

public interface AdapterObserver {

	public void onAdapterChanged(ArrayAdapter<Parking> adapter, ListView list);

}
