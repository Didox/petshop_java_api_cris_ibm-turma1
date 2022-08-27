package com.br.petshop.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.br.petshop.Model.Animal;
import com.br.petshop.Model.Cliente;
import com.br.petshop.Service.IAnimalService;
import com.br.petshop.Service.IClienteService;

@RestController
@RequestMapping(path="/petshop")
public class ClienteController {

	@Autowired
	private IClienteService service;
	
	@Autowired
	private IAnimalService animalService;

	@GetMapping("/clientes/{id}")
	public ResponseEntity<Cliente> buscar(@PathVariable("id") Integer id) {
		Cliente cliente = service.buscar(id);
		if (cliente != null) {
			return ResponseEntity.ok(cliente);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/clientes")
	public ArrayList<Cliente> listar() {
		return (ArrayList<Cliente>) service.listar();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@PostMapping("/clientes")
	public ResponseEntity<Cliente> novo(@Valid @RequestBody Cliente cliente) {
		try{
			Cliente pet = service.salvar(cliente);
			if (pet != null) {
				return ResponseEntity.ok(pet);
			}
			return ResponseEntity.badRequest().build();
		}
		catch(Exception err){
			return ResponseEntity.status(400).build();
		}
		
	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<Cliente> atualizado(@PathVariable("id") Integer id, @RequestBody Cliente cliente) {
		Cliente registroCliente = service.buscar(id);
		if (registroCliente != null) {
			registroCliente.setAnimais(cliente.getAnimais());
			registroCliente.setNome(cliente.getNome());
			registroCliente.setTelefone(cliente.getTelefone());
			service.salvar(registroCliente);
			return ResponseEntity.ok(registroCliente);
		}
		return ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/clientes/{id}")
	public void excluir(@PathVariable("id") Integer id) {
		Cliente registroCliente = service.buscar(id);
		if (registroCliente != null) {
			ArrayList<Animal> animais = (ArrayList<Animal>) registroCliente.getAnimais();
			for (Animal animal : animais) {
				animalService.excluir(animal.getId());
			}
			service.excluir(id);
		}
	}
}
