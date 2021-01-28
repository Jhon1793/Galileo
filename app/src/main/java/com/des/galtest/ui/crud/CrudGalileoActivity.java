package com.des.galtest.ui.crud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.des.galtest.R;
import com.des.galtest.model.SitioWeb;
import com.des.galtest.ui.auth.register.RegisterViewModel;
import com.des.galtest.utils.LoadingDialog;
import com.des.galtest.utils.RxBindingHelper;
import com.des.galtest.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class CrudGalileoActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private CrudGalileoViewModel crudGalileoViewModel;
    private TextInputEditText sitioweb,encargado;
    private Button add,update,delete;
    private Observable<Boolean> formObservable;
    private  ListView listV_Sitio;
    ArrayAdapter<SitioWeb> arrayAdapterSitio;
   private  List<SitioWeb> listSitio= new ArrayList<SitioWeb>();
    private SitioWeb sw;
    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_galileo);
        initview();

       // formValidation();
        listarDatos();
        crudGalileoViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(CrudGalileoViewModel.class);
        subscribeObservers();
        //listV_Sitio.setOnItemClickListener((adapterView, view, i, l) ->sw=crudGalileoViewModel.selectOne(adapterView,i));
        listV_Sitio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sw=crudGalileoViewModel.selectOne(adapterView,i);
                sitioweb.setText(sw.getNombreSitio().toString());
                encargado.setText(sw.getEncargado().toString());

            }
        });
    }

    private void subscribeObservers() {
        crudGalileoViewModel.observeCrud().observe(this, stateResource -> {
            if(stateResource != null){
                switch (stateResource.status){
                    case LOADING:
                        loadingDialog.show(getSupportFragmentManager(),"loadingDialog");
                        break;
                    case SUCCESS:
                        loadingDialog.dismiss();
                        ///moveToHomeActivity();
                        listarDatos();
                        break;
                    case ERROR:
                        loadingDialog.dismiss();
                        Log.d("CRUD", "subscribeObservers: Error");
                        break;
                }
            }
        });


    }

    private void formValidation() {
        Observable<String> nombreSitio = RxBindingHelper.getObservableFrom(sitioweb);
        Observable<String>  encargadoS = RxBindingHelper.getObservableFrom(encargado);

        formObservable = Observable.combineLatest(nombreSitio,encargadoS, this::isValidForm);

        formObservable.subscribe(new DisposableObserver<Boolean>() {

            @Override
            public void onNext(@NonNull Boolean aBoolean) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private Boolean isValidForm(String nombreSitio, String encargadoS) {
        boolean isName = nombreSitio.isEmpty();
        if (isName) {
            sitioweb.setError("Nombre no válido");
        }
        boolean isEncargado=encargadoS.isEmpty();
        if(isEncargado){
            encargado.setText("Encargado no valido");
        }

       return !isName && !isEncargado;
    }

    private void initview() {
        sitioweb=findViewById(R.id.sitioweb);
        encargado=findViewById(R.id.supervisor);
        add=(Button)findViewById(R.id.add);
        delete=(Button)findViewById(R.id.delete);
        update=(Button)findViewById(R.id.update);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        update.setOnClickListener(this);
        listV_Sitio= findViewById(R.id.lv_datoSitios);
    }

    private void listarDatos() {

       /// DatabaseReference databaseReference = crudGalileoViewModel.databaseReference();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Sitio");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                listSitio.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    SitioWeb g = objSnapshot.getValue(SitioWeb.class);
                    listSitio.add(g);
                    arrayAdapterSitio = new ArrayAdapter<SitioWeb>(CrudGalileoActivity.this, android.R.layout.simple_list_item_1, listSitio);
                    listV_Sitio.setAdapter(arrayAdapterSitio);
                }
            }


            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }





    private void limpiarCajas() {
        sitioweb.setText("");
        encargado.setText("");
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.add){
            addData();
        }else if(view.getId()==R.id.delete){
            deleteData();
        }else if(view.getId()==R.id.update){
            updateData();
        }
    }

    private void deleteData() {
      if(sw !=null){
        crudGalileoViewModel.deleteDatabaseRealtime(sw);
          Toast.makeText(this, "Eliminado", Toast.LENGTH_LONG).show();
          limpiarCajas();
      }

    }

    private void updateData() {
        String name = Objects.requireNonNull(sitioweb.getText()).toString();
        String encargad=Objects.requireNonNull(encargado.getText().toString());
        if(sw != null){
            if(isValidForm(name,encargad)) {
                crudGalileoViewModel.updateDatabaseRealtime(sw, name, encargad);
                Toast.makeText(this, "Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
            }
      }

    }

    private void addData() {
        String name = Objects.requireNonNull(sitioweb.getText()).toString();
        String encargad=Objects.requireNonNull(encargado.getText().toString());
        if(isValidForm(name,encargad)) {
            crudGalileoViewModel.addDatabaseRealtime(name, encargad);
            Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
            limpiarCajas();
        }
    }
}