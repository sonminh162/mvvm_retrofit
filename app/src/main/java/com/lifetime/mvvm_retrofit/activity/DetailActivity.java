package com.lifetime.mvvm_retrofit.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lifetime.mvvm_retrofit.R;
import com.lifetime.mvvm_retrofit.model.Employee;
import com.lifetime.mvvm_retrofit.model.EmployeeResponse;
import com.lifetime.mvvm_retrofit.viewmodels.EmployeeViewModel;

public class DetailActivity extends AppCompatActivity {
    EditText editTextName, editTextAge, editTextSalary;
    TextView currentId;
    Button buttonUpdate, buttonDelete;

    EmployeeViewModel employeeViewModel;
    Employee employeeReceived;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        editTextAge = findViewById(R.id.update_age);
        editTextName = findViewById(R.id.update_name);
        editTextSalary = findViewById(R.id.update_salary);
        currentId = findViewById(R.id.current_id);
        buttonUpdate = findViewById(R.id.button_update);
        buttonDelete = findViewById(R.id.button_delete);


        employeeReceived = (Employee) getIntent().getSerializableExtra("employee");

        employeeViewModel = ViewModelProviders.of(this).get(EmployeeViewModel.class);
        employeeViewModel.init();
        employeeViewModel.getEmployeeById(employeeReceived.getId()).observe(this,new Observer<Employee>() {
            @Override
            public void onChanged(Employee employee) {
                loadEmployee(employee);
                progressDialog.dismiss();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateEmployee(employeeReceived);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Are your sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEmployee(employeeReceived);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });

    }

    private void loadEmployee(Employee employee){
        editTextName.setText(employee.getName());
        editTextSalary.setText(employee.getSalary());
        editTextAge.setText(employee.getAge());
        currentId.setText("CurrentID: " + employee.getId());
    }

    private void updateEmployee(final Employee employee){
        final String sName = editTextName.getText().toString().trim();
        final String sSalary = editTextSalary.getText().toString().trim();
        final String sAge = editTextAge.getText().toString().trim();

        if (sName.isEmpty()) {
            editTextName.setError("name required");
            editTextName.requestFocus();
            return;
        }

        if (sSalary.isEmpty()) {
            editTextSalary.setError("salary required");
            editTextSalary.requestFocus();
            return;
        }

        if (sAge.isEmpty()) {
            editTextAge.setError("age required");
            editTextAge.requestFocus();
            return;
        }

        employeeViewModel.updateEmployee(employee.getId(),new EmployeeResponse(sName,sSalary,sAge),this);
    }

    private void deleteEmployee(final Employee employee){
        employeeViewModel.deleteEmployee(employee,this);
    }
}
