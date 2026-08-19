import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UserResponseDto} from '../../../../shared/models/UserModel/UserResponseDto';
import {UsersServices} from '../../../../core/service/UserService/users-services';
import {CommonModule} from '@angular/common';
import {UserUpdateDto} from '../../../../shared/models/UserModel/UserUpdateDto';
import {FormsModule} from '@angular/forms';
import {DeleteAccountDto} from '../../../../shared/models/UserModel/DeleteAccountDto';
import {Router} from '@angular/router';
import {disabled} from '@angular/forms/signals';
import {InputboxComponents} from '../../../../shared/components/inputbox.components/inputbox.components';
import {ButtonComponent} from '../../../../shared/components/button.component/button.component';
import {TranslocoPipe} from '@jsverse/transloco';


@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, InputboxComponents, ButtonComponent, TranslocoPipe],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css',
})
export class ProfilePageComponent implements OnInit{


  public user: UserResponseDto={
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    role: ''
  };

  public userToDelete: DeleteAccountDto={
    username:'',
    password:''
  }

  public showWindow:boolean = false;

  public isEditingField:boolean= false;

  public errorMessage: string | null = null;

  public profilePictureUpdate :boolean= false;

  selectedAvatar = "assets/icons/avatar4.webp"


  constructor(private userService:UsersServices, private cdr: ChangeDetectorRef,private router: Router) {}

  /**
   * Collect the user information when the component initialize
   */
  ngOnInit() {
    this.userService.getConnectedUser().subscribe({
      next: user =>{
        console.log(user);
        this.user=user;
        this.cdr.detectChanges();
      },
      error:(err)=>{
        console.log(err);
        console.log("Error occured");
      }
    })
    const savedAvatar = localStorage.getItem('selectedAvatar');
    if (savedAvatar) {
      this.selectedAvatar = savedAvatar;
    }
  }


  /**
   * Go to the change password page
   */
  public goToChangePassword(){
    this.router.navigate(['change-password'])
  }

  public goToEvaluatorProfile(){
    this.router.navigate(['/evaluator-profile'])
  }

  /**
   * Toggle the value of the isEditingField
   *
   * @modifies isEditingField
   * @effect toggle the value
   **/
  public toggleEdit() {
    this.isEditingField= !this.isEditingField
  }

  /**
   * Collect the user modified information and send them to backend
   *
   * @effect : modifies user information and send to the backend
   * */
  updateProfile(){

    let userToUpdate:UserUpdateDto ={
      username: this.user.username,
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email
    }

    this.userService.updateUser(userToUpdate).subscribe({
      next: (response) => {
        console.log('Update successful!', response);
        this.errorMessage=null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error while updating', err);
        this.errorMessage = err.error.message;
        this.cdr.detectChanges();
      }

    });

    this.toggleEdit();
  }


  /**
   *  Cancel the modifications and restore the original user information
   */
  public cancel(){

    console.log("In Cancel");

    this.userService.getConnectedUser().subscribe({
      next: user =>{
        this.user=user;
        console.log("Cancel",user);
        this.cdr.detectChanges();
      },
      error:(err)=>{
        console.log(err);
        console.log("Error occured");
      }
    })

    this.toggleEdit();

  }


  public openWindow(){
    this.showWindow= true;
  }

  public closeWindow(){
    this.showWindow=false;
    this.userToDelete.password='';
  }

  public delete(){
    this.userToDelete.username= this.user.username;
    this.userService.deleteUserAccount(this.userToDelete).subscribe({
      next: () => {
      this.closeWindow();
      this.router.navigate(['/login']);
      console.log('Account Delete Successfully');
    },
      error: () => {
      console.log('Error while deleting account');
    }
    })
  }

  /**
   *  Display the menu to update picture after the click
   */
  toggleMenu() {
    this.profilePictureUpdate = !this.profilePictureUpdate;
  }

  /**
   * @param : image path of the selected avatar
   * */
  selectAvatar(avatar: string) {
    this.selectedAvatar = avatar;
    localStorage.setItem('selectedAvatar', avatar);
    this.profilePictureUpdate = false;
  }


  protected readonly disabled = disabled;
}
