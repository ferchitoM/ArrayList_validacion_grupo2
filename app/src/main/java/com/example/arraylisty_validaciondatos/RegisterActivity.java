package com.example.arraylisty_validaciondatos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText documento;
    EditText nombres;
    EditText apellidos;
    EditText fechaNacimiento;
    EditText email;
    EditText contraseña;
    EditText confirmarContraseña;
    Button botonRegistrar;

    //Textos de error
    TextView documento_error;
    TextView nombres_error;
    TextView apellidos_error;
    TextView fechaNacimiento_error;
    TextView email_error;
    TextView contraseña_error;
    TextView confirmarContraseña_error;

    ArrayList<Error> errores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        documento = findViewById(R.id.documento);
        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        confirmarContraseña = findViewById(R.id.confirmarContraseña);
        fechaNacimiento = findViewById(R.id.fechaNacimiento);

        fechaNacimiento.setFocusableInTouchMode(false);
        fechaNacimiento.setOnClickListener(v -> {
            seleccionarFecha();
        });

        //Textos de error
        documento_error = findViewById(R.id.documento_error);
        nombres_error = findViewById(R.id.nombres_error);
        apellidos_error = findViewById(R.id.apellidos_error);
        fechaNacimiento_error = findViewById(R.id.fechaNacimiento_error);
        email_error = findViewById(R.id.email_error);
        contraseña_error = findViewById(R.id.contraseña_error);
        confirmarContraseña_error = findViewById(R.id.confirmarContraseña_error);


        botonRegistrar = findViewById(R.id.botonRegistrar);
        botonRegistrar.setOnClickListener(v -> {
            boolean todoOK = validarCampos();

            if(todoOK) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

        });

        inicializarErrores();

    }

    private boolean validarCampos() {

        boolean valido = true;
        String texto;

        for (Error e : errores) {

            EditText campo = e.campo;
            texto = campo.getText().toString();

            //Validar si el campo es requerido y está vacío, entonces hay un error
            if (e.requerido && texto.isEmpty()) {
                e.mostrarError("Este campo es obligatorio");
                valido = false;
            }

            //Si el campo no es requerido pero contiene información, debe ser validado
            if(!texto.isEmpty()) {

                //Validar si el campo contiene caracteres inválidos
                if (e.expresion != "" && !texto.matches(e.expresion)) {
                    e.mostrarError("Formato o caracteres inválidos");
                    valido = false;
                }

                //Validar si el campo contiene un número de caracteres fuera del rango
                else if ((e.min != 0 && texto.length() < e.min) ||
                        (e.max != 0 && texto.length() > e.max)){
                    e.mostrarError("Debe tener entre " + e.min + " y " + e.max + " caracteres");
                    valido = false;
                }

                else e.ocultarCampoError();
            }

        }

        //Validar si las contraseñas coinciden
        String c1 = contraseña.getText().toString();
        String c2 = confirmarContraseña.getText().toString();

        if (!c1.equals(c2)) {
            confirmarContraseña_error.setVisibility(TextView.VISIBLE);
            confirmarContraseña_error.setText("Las contraseñas no coinciden");
            valido = false;
        }

        //Retorna true si no hay errores en el formulario
        return valido;

    }

    private void inicializarErrores() {

        errores = new ArrayList<>();

        String exNombres = "^[a-zA-ZÀ-ÿ\\s]+$";
        String exCorreos = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        errores.add(new Error(documento,            "", 7, 10,  documento_error,           false));
        errores.add(new Error(nombres,              exNombres,   3, 50,  nombres_error,             true ));
        errores.add(new Error(apellidos,            exNombres,   3, 50,  apellidos_error,           true ));
        errores.add(new Error(fechaNacimiento,      "", 0, 0,   fechaNacimiento_error,     true ));
        errores.add(new Error(email,                exCorreos,   0, 50,  email_error,               false ));
        errores.add(new Error(contraseña,           "", 6, 12,  contraseña_error,          true ));
        errores.add(new Error(confirmarContraseña,  "", 6, 12,  confirmarContraseña_error, true ));

    }

    private void seleccionarFecha() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                fechaNacimiento.setText(selectedDate);
            }
        });

        newFragment.show(this.getSupportFragmentManager(), "Seleccionar fecha");
    }


    //Clase personalizada para gestionar los errores
    public class Error {

        EditText campo;
        TextView error_campo;
        String expresion;
        int min; //mínimo de caracteres
        int max; //máximo de caracteres
        boolean requerido;

        public Error(EditText campo, String expresion, int min, int max, TextView error_campo, boolean requerido) {

            this.campo = campo;
            this.expresion = expresion;
            this.min = min;
            this.max = max;
            this.error_campo = error_campo;
            this.requerido = requerido;
        }

        public void mostrarError(String mensaje) {
            error_campo.setVisibility(TextView.VISIBLE);
            error_campo.setText("*" + mensaje);
        }

        public void ocultarCampoError() {
            error_campo.setVisibility(TextView.INVISIBLE);
        }
    }

    //Clase para seleccionar fechas
    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }

    }
}

